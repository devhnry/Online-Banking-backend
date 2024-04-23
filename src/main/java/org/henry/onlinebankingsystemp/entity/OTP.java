package org.henry.onlinebankingsystemp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    @Id
    @GeneratedValue
    private Long id;
    private Long otpCode;
    private Boolean expired;

    private Long generatedTime;
    private Long expirationTime;
    private String expiresIn;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private Customer customer;

    @Override
    public String toString(){
        return "OTP: " + String.valueOf(otpCode);
    }

}

