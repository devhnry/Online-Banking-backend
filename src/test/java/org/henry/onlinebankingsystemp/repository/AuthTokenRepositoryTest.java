package org.henry.onlinebankingsystemp.repository;

import jakarta.persistence.EntityManager;
import org.henry.onlinebankingsystemp.entity.AuthToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthTokenRepositoryTest {
    @MockBean private RestTemplate restTemplate;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private TokenRepository underTest;

    static MySQLContainer<?> container =
            new MySQLContainer<>()
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("s3cret")
                    .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }


    static {
        container.start();
    }

//    @AfterEach
//    void setup() {
//        container.stop();
//    }

    @Test
    void notNull() throws Exception{
        assertNotNull(underTest);
        assertNotNull(entityManager);
        assertNotNull(dataSource);
        assertNotNull(testEntityManager);

        System.out.println(dataSource.getConnection().getMetaData().getDatabaseProductName());

        AuthToken authToken = new AuthToken();
        authToken.setId(1);
        authToken.setRevoked(false);
        authToken.setExpired(false);
        authToken.setToken("kjvslncmvlksnmkzjsvsz");
        authToken.setTokenType(TokenType.BEARER);
        authToken.setAdmin(null);
        authToken.setUsers(null);

        AuthToken result = underTest.save(authToken);
        assertNotNull(result.getId());
    }

    @Test
    @Sql(scripts = "/scripts/FIND_TOKEN_BY_CUSTOMER.sql")
    void findValidTokenByCustomer() {
        List<AuthToken> authTokens = underTest.findValidTokenByCustomer(1L);

        assertEquals(3, underTest.count());
        assertEquals(2, authTokens.size());

        underTest.findValidTokenByCustomer(1L).forEach(token -> {
                    System.out.println(token.getId());
                    System.out.println(token.getToken());
                    System.out.println(token.getExpired());
                    System.out.println(token.getUsers());
                }
        );
        assertEquals("LSVMSLMDZCKLMCS", authTokens.get(0).getToken());
        assertEquals("LSVMSLMDZCKLMDS", authTokens.get(1).getToken());
        assertEquals(1L, authTokens.get(1).getUsers().getCustomerId());
    }

    @Test
    @Sql(scripts = "/scripts/FIND_TOKEN_BY_ADMIN.sql")
    void findValidTokenByAdmin() {
        List<AuthToken> authTokens = underTest.findValidTokenByAdmin(1L);

        assertEquals(3, underTest.count());
        assertEquals(2, authTokens.size());

        underTest.findValidTokenByCustomer(1L).forEach(token -> {
                    System.out.println(token.getId());
                    System.out.println(token.getToken());
                    System.out.println(token.getExpired());
                    System.out.println(token.getUsers());
                }
        );
        assertEquals("LSVMSLMDZCKLMCS", authTokens.get(0).getToken());
        assertEquals("LSVMSLMDZCKLMDS", authTokens.get(1).getToken());
        assertEquals(1L, authTokens.get(1).getAdmin().getAdminId());
    }
}