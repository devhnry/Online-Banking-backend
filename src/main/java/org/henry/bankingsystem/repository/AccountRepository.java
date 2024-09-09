package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.Account;
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
