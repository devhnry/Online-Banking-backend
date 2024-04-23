package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Override
    Optional<Account> findById(Long account_id);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByCustomerId(Long userId);

}
