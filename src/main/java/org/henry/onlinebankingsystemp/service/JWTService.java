package org.henry.onlinebankingsystemp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.henry.onlinebankingsystemp.entity.Customer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

// To generate authToken, refreshToken
@Service
public class JWTService {

    private SecretKey key;
    private static final long EXPIRATION_TIME = 86400000; //24hrs

    public JWTService() {
        String secretString = "HFUKNSF09839IKSFV9348J39GHIUEWFJR9EF089WUJ4FNR9HG738UJW4ONEMSCO";
        byte[] keyByte = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyByte,"HmacSHA256");
    }

    public String createJWT(Customer customer) { return  generateToken(customer); }

    private String generateToken(Customer customer){
        HashMap<String, Object> claims = new HashMap<>();

        claims.put("customerId",customer.getCustomerId());
        claims.put("firstName",customer.getFirstName());
        claims.put("lastName",customer.getLastName());
        claims.put("email",customer.getEmail());
        claims.put("phoneNumber",customer.getPhoneNumber());

        return Jwts.builder()
                .claims(claims)
                .subject(customer.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, Customer customer){
        return Jwts.builder()
                .claims(claims)
                .subject(customer.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
