package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.RequestResponse;
import org.henry.onlinebankingsystemp.entity.Account;
import org.henry.onlinebankingsystemp.entity.Admin;
import org.henry.onlinebankingsystemp.entity.Role;
import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.repository.AccountRepository;
import org.henry.onlinebankingsystemp.repository.AdminRepository;
import org.henry.onlinebankingsystemp.repository.TokenRepository;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.henry.onlinebankingsystemp.token.Token;
import org.henry.onlinebankingsystemp.token.TokenType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final AdminRepository adminRepository;

    public AuthenticationService(
            AccountRepository accountRepository,
            UserRepository userRepository,
            JWTService jwtService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository,
            AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
    }

    private Long generateRandom8DigitNumber() {
        return new Random().nextLong(100000000L);
    }

    public RequestResponse signUp(RequestResponse registrationRequest){
        RequestResponse resp = new RequestResponse();
        try {
            Users users = new Users();
            Admin admin = new Admin();
            Account userAccount = new Account();

            if (registrationRequest.getRole().equals(Role.ADMIN.toString())) {
                Optional<Admin> adminOptional = adminRepository.findByEmail(registrationRequest.getEmail());
                if (adminOptional.isPresent()) {
                    resp.setStatusCode(500);
                    resp.setError("Email Already Taken");
                    return resp;
                }

                admin.setFirstName(registrationRequest.getFirst_name());
                admin.setLastName(registrationRequest.getLast_name());
                admin.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
                admin.setEmail(registrationRequest.getEmail());
                admin.setRole(Role.valueOf(registrationRequest.getRole()));
                Admin adminResult = adminRepository.save(admin);

                if (adminResult.getAdminId() > 0) {
                    resp.setAdmin(adminResult);
                    resp.setFull_name(adminResult.getFirstName() + " " + adminResult.getLastName());
                    resp.setMessage("Successful Signup ... (Admin save to the db)");
                    resp.setStatusCode(200);
                }
            }else{

                Optional<Users> userOptional = userRepository.findByEmail(registrationRequest.getEmail());
                if (userOptional.isPresent()) {
                    resp.setStatusCode(500);
                    resp.setError("Email Already Taken");
                    return resp;
                }

                users.setEmail(registrationRequest.getEmail());
                users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
                users.setRole(Role.valueOf("USER"));
                users.setFirst_name(registrationRequest.getFirst_name());
                users.setLast_name(registrationRequest.getLast_name());
                users.setPhone_number(registrationRequest.getPhone_number());
                users.setAccount_type(registrationRequest.getAccount_type());
                users.setTransactionLimit(200_000);
                users.setIsSuspended(false);
                Users usersResult = userRepository.save(users);

                //For the account details
                userAccount.setUser_id(users.getUserId());
                userAccount.setBalance(0);
                userAccount.setAccountNumber(generateRandom8DigitNumber());

                //Saving into the database
                accountRepository.save(userAccount);
                users.setAccount_details(userAccount);
                userAccount.setAccount_type(users.getAccount_type());
                userRepository.save(users);

                if(usersResult.getUserId() > 0){
                    resp.setUsers(usersResult);
                    resp.setFull_name(usersResult.getFirst_name() + " " + usersResult.getLast_name());
                    resp.setMessage("Successful Signup ... (User save to the db)");
                    resp.setStatusCode(200);
                }
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RequestResponse signIn(RequestResponse signinRequest) {
        RequestResponse response = new RequestResponse();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getEmail(), signinRequest.getPassword()));

            Optional<Admin> optionalAdmin = adminRepository.findByEmail(signinRequest.getEmail());
            if (optionalAdmin.isPresent()) {
                var admin = optionalAdmin.orElseThrow();
                var jwtToken = jwtService.generateToken(admin);
                jwtService.generateRefreshToken(new HashMap<>(), admin);

                response.setStatusCode(200);
                response.setEmail(signinRequest.getEmail());
                response.setToken(jwtToken);
                response.setRefreshToken(jwtToken);
                response.setExpirationTime("24hr");
                response.setMessage("Successfully Signed In");

                // Revoke tokens for admin
                revokeAllAdminTokens(admin);

                // Save active token for admin
                saveAdminToken(admin, jwtToken);
            } else {
                Optional<Users> optionalUser = userRepository.findByEmail(signinRequest.getEmail());
                // Check if the admin exists
                if (optionalUser.isPresent()) {
                    var user = optionalUser.orElseThrow();

                    if(user.getIsSuspended()){
                        response.setMessage("Your account has been suspended");
                        response.setStatusCode(500);
                        return response;
                    }

                    var jwtToken = jwtService.generateToken(user);
                    jwtService.generateRefreshToken(new HashMap<>(), user);

                    response.setStatusCode(200);
                    response.setEmail(signinRequest.getEmail());
                    response.setToken(jwtToken);
                    response.setRefreshToken(jwtToken);
                    response.setExpirationTime("24hr");
                    response.setMessage("Successfully Signed In");

                    // Revoke tokens for admin (if needed)
                    revokeAllUserTokens(user);

                    // Save token for admin
                    saveUserToken(user, jwtToken);


                } else {
                    // If neither user nor admin exists
                    response.setStatusCode(404);
                    response.setError("User or Admin not found");
                    return response;
                }
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }

        return response;
    }

    public RequestResponse refreshToken(RequestResponse refreshTokenRequest){
        RequestResponse res = new RequestResponse();
        String userEmail = jwtService.extractUsername(refreshTokenRequest.getToken());
        Users user = userRepository.findByEmail(userEmail).orElseThrow();

        try {
            Optional<Users> optionalUser = userRepository.findByEmail(userEmail);
            if(optionalUser.isPresent()){
                if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)){
                    var newToken = jwtService.generateToken(user);

                    revokeAllUserTokens(user);
                    saveUserToken(user,newToken);

                    res.setStatusCode(200);
                    res.setToken(newToken);
                    res.setRefreshToken(refreshTokenRequest.getToken());
                    res.setExpirationTime("24hr");
                    res.setMessage("Successfully Refreshed Token");
                }
            }
            else {
                Admin admin = adminRepository.findByEmail(userEmail).orElseThrow();
                Optional<Admin> optionalAdmin = adminRepository.findByEmail(userEmail);
                if (optionalAdmin.isPresent()) {
                    if(jwtService.isTokenValid(refreshTokenRequest.getToken(), admin)){
                        var newToken = jwtService.generateToken(admin);

                        revokeAllUserTokens(user);
                        saveUserToken(user,newToken);

                        res.setStatusCode(200);
                        res.setToken(newToken);
                        res.setRefreshToken(refreshTokenRequest.getToken());
                        res.setExpirationTime("24hr");
                        res.setMessage("Successfully Refreshed Token");
                    }
                }
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    public void saveUserToken(Users user, String newToken) {
        var token = Token.builder()
                .users(user)
                .token(newToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public void saveAdminToken(Admin admin, String newToken){
        var token = Token.builder()
                .admin(admin)
                .token(newToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(Users user){
        var validUserTokens = tokenRepository.findValidTokenByUsers(user.getUserId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void revokeAllAdminTokens(Admin admin){
        var validAdminToken = tokenRepository.findValidTokenByAdmin(admin.getAdminId());
        System.out.println(validAdminToken);
        if(validAdminToken.isEmpty())
            return;
        validAdminToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validAdminToken);
    }
}
