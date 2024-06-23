package com.venuehub.bookingservice.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers

public abstract class AbstractContainerBaseTest {

    // Define a MySQLContainer instance
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
            .withDatabaseName("bookingservice")
            .withUsername("Ali")
            .withPassword("@Li4103568")
            ;

    @BeforeAll
    public static void startContainer() {
        // Start the container before any test runs
        mysqlContainer.start();

        // Set JDBC URL, username and password for tests
        System.setProperty("DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", mysqlContainer.getUsername());
        System.setProperty("DB_PASSWORD", mysqlContainer.getPassword());
    }

    @AfterAll
    public static void stopContainer() {
        // Stop the container after all tests have run
        mysqlContainer.stop();
    }
}