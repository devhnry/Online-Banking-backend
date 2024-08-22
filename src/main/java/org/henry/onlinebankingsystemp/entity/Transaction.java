package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.henry.onlinebankingsystemp.enums.TransactionCategory;
import org.henry.onlinebankingsystemp.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false, unique = true)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory transactionCategory;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private BigDecimal runningBalance;

    @Column(nullable = false)
    private BigDecimal balanceAfterRunningBalance;

    @Column(nullable = false)
    private String targetAccountNumber;
}
