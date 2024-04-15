package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Transaction;
import org.henry.onlinebankingsystemp.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("""
            select t from Transaction t inner join Users u on t.account.user_id = u.userId
            where u.userId = :userId
        """
    )
    List<Transaction> findTransactionByUsers(Long userId);
    @Override
    Optional<Transaction> findById(Long id);
}
