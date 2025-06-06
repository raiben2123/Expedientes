package com.ruben.Expedientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpedientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpedientesApplication.class, args);
	}

}
