package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Admin;
import org.henry.onlinebankingsystemp.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
            select t from Token t inner join Users u on t.users.userId = u.userId
            where u.userId = :userId and (t.expired = false or t.revoked = false)
        """
    )
    List<Token> findValidTokenByUsers(Long userId);

    @Query("""
            select t from Token t inner join Admin a on t.admin.adminId = a.adminId
            where a.adminId = :adminId and (t.expired = false or t.revoked = false)
        """
    )
    List<Token> findValidTokenByAdmin(Long adminId);

    Optional<Token> findByToken(String token);
}
