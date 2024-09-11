package org.henry.bankingsystem.repository;

import org.henry.bankingsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    Optional<Customer> findByEmail(String email);
    Customer findCustomerByEmail(String email);
}
