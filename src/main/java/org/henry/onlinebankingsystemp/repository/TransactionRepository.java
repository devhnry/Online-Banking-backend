package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            select t from Transaction t inner join Customer u on t.account.customer.customerId = u.customerId
            where u.customerId = :customerId
        """
    )
    List<Transaction> findTransactionByCustomer(Long customerId);
    @NotNull Optional<Transaction> findById(@NotNull Long id);
    boolean existsByTransactionRef(String transactionReference);
}
