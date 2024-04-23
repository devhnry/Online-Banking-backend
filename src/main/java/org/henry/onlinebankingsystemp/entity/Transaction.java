package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.henry.onlinebankingsystemp.dto.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String transactionRef;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;
    private LocalDateTime transactionDate;

    private BigDecimal runningBalance;
    private BigDecimal balanceAfterRunningBalance;

    private BigDecimal credit;
    private BigDecimal debit;

    @Column(name = "target_account_number")
    private String targetAccountNumber;
}

