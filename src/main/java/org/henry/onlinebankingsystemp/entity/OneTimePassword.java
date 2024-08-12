package org.henry.onlinebankingsystemp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "customer")
public class OneTimePassword {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long otpCode;

    @Column(nullable = false)
    private Boolean expired;

    @Column(nullable = false)
    private Instant generatedTime;

    @Column(nullable = false)
    private Instant expirationTime;

    @Column(nullable = false)
    private String expiresDuration;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Customer customer;
}