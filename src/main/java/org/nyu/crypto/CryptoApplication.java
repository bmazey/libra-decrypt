package org.nyu.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories("org.nyu.crypto.service.data")
public class CryptoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}
}
