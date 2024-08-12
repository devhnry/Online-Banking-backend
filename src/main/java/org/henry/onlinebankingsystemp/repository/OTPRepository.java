package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.entity.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findById(Long id);
    Optional<OneTimePassword> findByCustomerAndOtpCode(Customer customer, Long otpCode );
}
