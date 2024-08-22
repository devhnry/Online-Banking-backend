package org.henry.onlinebankingsystemp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"customer", "admin"})
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String accessToken;

    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;

    @Builder.Default
    @Column(nullable = false)
    private Boolean expired = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revoked = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "customerId")
    private Customer customer;

    @JsonIgnore @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId", referencedColumnName = "adminId")
    private Admin admin = null;
}