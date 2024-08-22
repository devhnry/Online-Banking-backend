package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private BigDecimal accountBalance;

    @Column(nullable = false)
    private BigDecimal transactionLimit;

    @Column(nullable = false)
    private Instant dateOpened;

    @Column(nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currencyType;

    private BigDecimal interestRate;

    private Instant lastTransactionDate;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> cards;
}
