package org.henry.onlinebankingsystemp;

import io.jsonwebtoken.JwtBuilder;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OnlineBankingSystemPApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineBankingSystemPApplication.class, args);
    }

}
