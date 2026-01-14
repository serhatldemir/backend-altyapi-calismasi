package com.staj.backend_gorev;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tüm entegrasyon testleri için temel sınıf.
 * Bu sınıfı extend eden tüm testler otomatik olarak gerçek PostgreSQL ve Redis
 * üzerinde çalışır.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

        // 1. PostgreSQL Konteyneri
        @Container
        @ServiceConnection // Spring Boot, jdbc url, username ve password'ü otomatik bağlar.
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

        // 2. Redis Konteyneri
        @Container
        @ServiceConnection(name = "redis") // 'redis' ismi sayesinde Spring Data Redis ayarları otomatik yapılır.
        static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
                        .withExposedPorts(6379);

        /*
         * NOT: @ServiceConnection(name = "redis") kullandığın an Spring Boot,
         * test sırasında "spring.data.redis.host" ve "port" bilgilerini
         * bu konteynerin dinamik değerleriyle otomatik eşleştirir.
         */
}