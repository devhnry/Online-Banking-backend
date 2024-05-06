package org.henry.onlinebankingsystemp.repository;

import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.henry.onlinebankingsystemp.controller.AccountController;
import org.henry.onlinebankingsystemp.service.AccountService;
import org.henry.onlinebankingsystemp.service.JWTService;
import org.henry.onlinebankingsystemp.service.UserDetailService;
import org.henry.onlinebankingsystemp.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenRepositoryTest {

    @MockBean private RestTemplate restTemplate;

//    @Configuration
    static MySQLContainer<?> container =
            new MySQLContainer<>("mysql:5.7.34")
                    .withDatabaseName("OnlineBanking")
                    .withUsername("test")
                    .withPassword("s3cret")
                    .withReuse(true);

    static {
        container.start();
    }

    @Test
    void findValidTokenByCustomer() {
    }

    @Test
    void findValidTokenByAdmin() {
    }
}