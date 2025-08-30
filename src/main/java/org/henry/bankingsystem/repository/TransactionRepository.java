package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @NotNull Optional<Transaction> findById(@NotNull Long id);
    boolean existsByTransactionRef(String transactionReference);
    List<Transaction> findAllByCustomer_CustomerIdAndTransactionDateContains(String customer_customerId, String transactionDate);
    List<Transaction> findAllByCustomer_CustomerIdOrderByTransactionIdDesc(String customer_customerId);
}
