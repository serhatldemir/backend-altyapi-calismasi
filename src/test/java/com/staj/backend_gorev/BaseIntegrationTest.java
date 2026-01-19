package com.staj.backend_gorev;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Tüm entegrasyon testleri için temel sınıf.
 * Bu sınıfı extend eden tüm testler otomatik olarak gerçek PostgreSQL ve Redis
 * üzerinde çalışır.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

        // 1. PostgreSQL Konteyneri
        // Yerel veritabanınla (16.4) uyumlu olması için sürümü 16-alpine olarak
        // güncelledik.
        @Container
        @ServiceConnection
        @SuppressWarnings("resource")
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                        DockerImageName.parse("postgres:16-alpine"));

        // 2. Redis Konteyneri
        @Container
        @ServiceConnection(name = "redis")
        @SuppressWarnings("resource")
        static GenericContainer<?> redis = new GenericContainer<>(
                        DockerImageName.parse("redis:7-alpine"))
                        .withExposedPorts(6379);

        /*
         * NOT: @ServiceConnection sayesinde Spring Boot, test sırasında
         * veritabanı ve redis bağlantı bilgilerini otomatik olarak ayarlar.
         * * Bu yapı ile Liquibase otomatik olarak devreye girer ve her test başında
         * test konteynerı içinde tabloları sıfırdan oluşturur.
         */
}