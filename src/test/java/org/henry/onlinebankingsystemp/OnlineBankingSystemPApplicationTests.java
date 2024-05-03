package org.henry.onlinebankingsystemp;

import io.jsonwebtoken.JwtBuilder;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootTest
class OnlineBankingSystemPApplicationTests {

    @Test
    void contextLoads() {
    }
}
