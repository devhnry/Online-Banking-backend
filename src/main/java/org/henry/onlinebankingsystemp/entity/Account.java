package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 2
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long account_id;

    // One Account belongs to One User
    @JoinColumn(name = "user_id", nullable = false)
    private Long user_id;

    private double balance;
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType account_type;
}
