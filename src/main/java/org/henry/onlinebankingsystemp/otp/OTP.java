package org.henry.onlinebankingsystemp.otp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.henry.onlinebankingsystemp.entity.Admin;
import org.henry.onlinebankingsystemp.entity.Users;

import java.util.Date;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Override
    public String toString(){
        return "OTP: " + String.valueOf(otpCode);
    }

}

