package org.henry.onlinebankingsystemp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.henry.onlinebankingsystemp.enums.AccountType;
import org.henry.onlinebankingsystemp.enums.CurrencyType;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
