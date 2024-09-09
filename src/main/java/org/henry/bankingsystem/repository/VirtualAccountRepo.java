package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.VirtualAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualAccountRepo extends JpaRepository<VirtualAccount, String> {
    List<VirtualAccount> findByCustomer_CustomerId(String customerId);
    Optional<VirtualAccount> findByAccountNumber(String accountNumber);
}

