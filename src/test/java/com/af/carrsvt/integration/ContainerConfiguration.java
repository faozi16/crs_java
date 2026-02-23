package com.af.carrsvt.integration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

@Configuration
public class ContainerConfiguration {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("car_rsvt_test")
            .withUsername("testuser")
            .withPassword("testpass");
    }
}
