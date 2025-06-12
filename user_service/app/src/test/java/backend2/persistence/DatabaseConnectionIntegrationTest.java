package backend2.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Minimal integration test to verify PostgreSQL database connectivity.
 */
@SpringBootTest(classes = backend2.App.class)
@ActiveProfiles("test")
public class DatabaseConnectionIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void whenDatabaseConnection_thenConnectionIsValid() {
        assertDoesNotThrow(() -> {
            try (Connection connection = dataSource.getConnection()) {
                // If we get here, the connection is valid
            }
        });
    }
} 