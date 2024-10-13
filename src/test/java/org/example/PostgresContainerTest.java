package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostgresContainerTest {

    private static PostgreSQLContainer<?> postgresContainer;
    private static Connection connection;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Start PostgreSQL container
        postgresContainer = new PostgreSQLContainer<>("postgres:15.1")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("password");
        postgresContainer.start();

        // Establish a JDBC connection to the container
        connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        // Create database and table if they don't exist
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS test_table ("
                    + "id SERIAL PRIMARY KEY, "
                    + "name VARCHAR(50) NOT NULL)";
            statement.execute(createTableQuery);
        }
    }

    @Test
    public void testPostgresContainer() throws SQLException {
        System.out.println("Sample test for Postgres using TestContainer");

        // Perform a simple query to verify the table exists
        try (Statement statement = connection.createStatement()) {
            String insertQuery = "INSERT INTO test_table (name) VALUES ('Test Name')";
            int rowsAffected = statement.executeUpdate(insertQuery);
            assertTrue(rowsAffected > 0, "Record inserted successfully into the test_table.");
        }
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }
}
