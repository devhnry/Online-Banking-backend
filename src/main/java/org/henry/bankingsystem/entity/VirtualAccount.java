package org.henry.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class VirtualAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String vAccountId;

    private String uniqueId;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountReference;

    @Column(nullable = false)
    private String accountStatus;

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;
}
