package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.otp.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findById(Long id);

    Optional<OTP> findByUsersAndOtpCode(Users user, Long otpCode );
}
