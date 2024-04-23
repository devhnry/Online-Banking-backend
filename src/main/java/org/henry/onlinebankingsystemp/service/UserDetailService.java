package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.repository.AdminRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public UserDetailService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            return userOptional.orElseThrow();
        }

        // If not found in the customer repository, check the admin repository
        var adminOptional = adminRepository.findByEmail(username);
        if (adminOptional.isPresent()) {
            return adminOptional.orElseThrow();
        }

        throw new UsernameNotFoundException("Customer or admin not found with email: " + username);
    }
}
