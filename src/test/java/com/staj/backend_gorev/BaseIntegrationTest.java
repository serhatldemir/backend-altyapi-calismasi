package com.staj.backend_gorev;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection; // SİHİRLİ SATIR 1
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    @ServiceConnection // SİHİRLİ SATIR 2: Bütün manuel ayarları çöpe atan notasyon
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // BİTTİ!
    // Ne @DynamicPropertySource var, ne de registry.add ameleliği...
}