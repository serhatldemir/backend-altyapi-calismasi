package com.staj.backend_gorev;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Tüm entegrasyon testleri için temel sınıf.
 * Bu sınıfı extend eden tüm testler otomatik olarak gerçek PostgreSQL ve Redis
 * üzerinde çalışır. DBUnit desteği ve FlatXmlDataSetLoader yapılandırması
 * eklenmiştir.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestExecutionListeners({
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextTestExecutionListener.class,
                TransactionalTestExecutionListener.class,
                DbUnitTestExecutionListener.class // DBUnit'i tetikleyen ana sınıf
})
@DbUnitConfiguration(dataSetLoader = FlatXmlDataSetLoader.class) // XML formatını doğru okuması için eklendi
public abstract class BaseIntegrationTest {

        // 1. PostgreSQL Konteyneri
        // Yerel veritabanınla (16.4) uyumlu olması için sürümü 16-alpine olarak
        // ayarlandı.
        @Container
        @ServiceConnection
        @SuppressWarnings("resource")
        protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                        DockerImageName.parse("postgres:16-alpine"));

        // 2. Redis Konteyneri
        @Container
        @ServiceConnection(name = "redis")
        @SuppressWarnings("resource")
        protected static final GenericContainer<?> redis = new GenericContainer<>(
                        DockerImageName.parse("redis:7-alpine"))
                        .withExposedPorts(6379);

        /*
         * NOT: @ServiceConnection sayesinde Spring Boot, test sırasında
         * veritabanı ve redis bağlantı bilgilerini otomatik olarak ayarlar.
         * Bu yapı ile Liquibase otomatik olarak devreye girer ve her test başında
         * test konteynerı içinde tabloları sıfırdan oluşturur.
         * DBUnit ise bu tabloları test verileriyle (dataset) doldurmanıza olanak tanır.
         */
}