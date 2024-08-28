package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findAccountByCustomer_CustomerId(String customerId);
    Optional<Account> findAccountByCustomer_Email(String email);
}
