package com.staj.backend_gorev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // <-- YENİ EKLENDİ

@SpringBootApplication
@EnableCaching // <-- KRİTİK NOKTA: Caching sistemini (Redis'i) aktif ettik.
public class BackendGorevApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendGorevApplication.class, args);
	}
}