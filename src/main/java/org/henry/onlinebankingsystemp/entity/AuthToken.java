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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private Boolean expired = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revoked = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private Customer customer;

    @JsonIgnore @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId", referencedColumnName = "adminId")
    private Admin admin = null;
}