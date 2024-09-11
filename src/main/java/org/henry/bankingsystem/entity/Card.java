package org.henry.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.henry.bankingsystem.enums.CardType;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, unique = true)
    private String cvv;

    @Version
    private Long version;
}
