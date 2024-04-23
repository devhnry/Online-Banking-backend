package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.VirtualAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualAccountRepo extends JpaRepository<VirtualAccount, Long> {
    List<VirtualAccount> findByCustomerId(Long customerId);
    Optional<VirtualAccount> findByAccountNumber(String accountNumber);
}

