package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.henry.onlinebankingsystemp.enums.AdminRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity @Builder
@AllArgsConstructor @NoArgsConstructor
@Setter @Getter @ToString
@Table(name = "admins")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String adminId;

    private String firstName;

    private String lastName;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    private AdminRoles role;

    @OneToMany(mappedBy = "admin")
    private List<AuthToken> authToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Grant authority based on the admins role
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}