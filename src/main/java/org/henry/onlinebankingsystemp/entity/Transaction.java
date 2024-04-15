package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING) // Store transaction type as string
    private TransactionType transactionType;

    private Double amount;

    private Date dateTime;

    private String description;

    @Column(name = "target_account_number")
    private String targetAccountNumber;
}

