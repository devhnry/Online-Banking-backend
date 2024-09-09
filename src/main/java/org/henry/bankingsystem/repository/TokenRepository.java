package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<AuthToken, Long> {

    @Query("""
            select t from AuthToken t inner join Customer u on t.customer.customerId = u.customerId
            where u.customerId = :customerId and (t.expired = false or t.revoked = false)
        """
    )
    List<AuthToken> findValidTokenByCustomer(String customerId);
    @Query("""
            select t from AuthToken t inner join Admin a on t.admin.adminId = a.adminId
            where a.adminId = :adminId and (t.expired = false or t.revoked = false)
        """
    )
    List<AuthToken> findValidTokenByAdmin(Long adminId);
    Optional<AuthToken> findByAccessToken(String authToken);
}
