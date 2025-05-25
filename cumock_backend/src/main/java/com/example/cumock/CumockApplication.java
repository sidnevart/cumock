package com.example.cumock;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Base64;

@SpringBootApplication(scanBasePackages = "com.example.cumock")
@EnableJpaRepositories(basePackages = "com.example.cumock.repository")
@EntityScan(basePackages = "com.example.cumock.model")
@EnableScheduling
public class CumockApplication {




	public static void main(String[] args) {
		String key = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
		System.out.println("Key: " + key);
		SpringApplication.run(CumockApplication.class, args);
	}

}