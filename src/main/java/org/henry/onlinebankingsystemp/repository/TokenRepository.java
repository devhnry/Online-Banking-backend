package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<AuthToken, Integer> {

    @Query("""
            select t from AuthToken t inner join Customer u on t.users.customerId = u.customerId
            where u.customerId = :userId and (t.expired = false or t.revoked = false)
        """
    )
    List<AuthToken> findValidTokenByCustomer(Long userId);
    @Query("""
            select t from AuthToken t inner join Admin a on t.admin.adminId = a.adminId
            where a.adminId = :adminId and (t.expired = false or t.revoked = false)
        """
    )
    List<AuthToken> findValidTokenByAdmin(Long adminId);
    Optional<AuthToken> findByToken(String authToken);
}
