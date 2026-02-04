package com.Ypisds.eservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class EservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EservicesApplication.class, args);
	}

}
