package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findByOtpCode(String otp);
}
