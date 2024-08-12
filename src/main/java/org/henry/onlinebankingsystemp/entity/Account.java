package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.henry.onlinebankingsystemp.enums.AccountType;

import java.math.BigDecimal;

@Data
@Entity
@Getter
@Setter
@ToString
@Table(name = "accounts")
public class Account {
    @Id
    @SequenceGenerator(
            name = "accountSeq",
            sequenceName = "accountSeq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;

    @JoinColumn(name = "customerId", nullable = false)
    private Long customerId;

    private BigDecimal balance;
    @Column(unique = true)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType account_type;
    private BigDecimal transactionLimit;
}
