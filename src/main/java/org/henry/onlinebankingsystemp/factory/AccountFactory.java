package org.henry.onlinebankingsystemp.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.dto.SignUpDTO;
import org.henry.onlinebankingsystemp.enums.Role;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Admin;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.AdminRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.service.utils.AccountNumberGenerator;
import org.henry.onlinebankingsystemp.service.utils.PasswordValidation;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountFactory {

    private final AccountNumberGenerator accountNumberGenerator;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordValidation passwordValidation;
    private final PasswordEncoder passwordEncoder;

    DefaultResponse res = new DefaultResponse();

    public DefaultResponse createAccount(SignUpDTO request){
        return signUp(request);
    }

    private String generateAccountNumber() {
        return accountNumberGenerator.generateAccountNumber();
    }

    private DefaultResponse signUp(SignUpDTO signUpRequest){
        try {
            if (signUpRequest.getRole() == Role.ADMIN){
                return adminSignUp(signUpRequest);
            }

            Customer customer = new Customer();
            Account account = new Account();

            boolean userAlreadyExist = userRepository.findByEmail(signUpRequest.getEmail()).isPresent();

            checkPasswordAndEmail(userAlreadyExist, signUpRequest.getPassword());

            log.info("Creating Customer Account");
            customer.setFirstName(signUpRequest.getFirstName());
            customer.setLastName(signUpRequest.getLastName());
            customer.setEmail(signUpRequest.getEmail());
            customer.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            customer.setPhone(signUpRequest.getPhone());
            customer.setUsername(signUpRequest.getEmail());
            customer.setRole(Role.USER);
            customer.setIsSuspended(false);
            userRepository.save(customer);

            log.info("Creating account for Customer");
            account.setCustomerId(customer.getCustomerId());
            account.setBalance(BigDecimal.valueOf(0.0));
            account.setAccount_type(signUpRequest.getAccountType());
            account.setTransactionLimit(BigDecimal.valueOf(200000));
            account.setAccountNumber(generateAccountNumber());
            accountRepository.save(account);

            customer.setAccount(account);
            accountRepository.save(account);

            log.info("Account created and saved");
            res.setStatusCode(200);
            res.setMessage("Successful Signup...");
            res.setData(customer);
        }catch (ConstraintViolationException e) {
            res.setStatusCode(500);
            res.setMessage("Email Already In Use");
        }
//        }catch (Exception e){
//            res.setStatusCode(500);
//            res.setMessage(e.getMessage());
//        }
        return res;
    }

    private DefaultResponse adminSignUp(SignUpDTO request){
        try {
            Admin admin = new Admin();
            boolean adminAlreadyExist = adminRepository.findByEmail(request.getEmail()).isPresent();

            checkPasswordAndEmail(adminAlreadyExist, request.getPassword());

            log.info("Creating Admin Account");
            admin.setFirstName(request.getFirstName());
            admin.setLastName(request.getLastName());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setUsername(request.getEmail());
            admin.setRole(Role.ADMIN);

            adminRepository.save(admin);

            log.info("Account created and saved for Admin");
            res.setStatusCode(200);
            res.setMessage("Successful Signup...");
            res.setData(admin);
        }catch (Exception e){
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    private DefaultResponse checkPasswordAndEmail(boolean accountExist, String password) {
        DefaultResponse res = new DefaultResponse();
        if (accountExist) {
            res.setStatusCode(500);
            res.setMessage("Email Already Taken");
            return res;
        }

        log.info("Checking password Strength");
        if (!passwordValidation.verifyPasswordStrenght(password)) {
            log.error("Password not strong enough");
            res.setStatusCode(500);
            res.setMessage("Password should contain at least 8 characters,numbers and a symbol");
            return res;
        }
        return res;
    }
}
