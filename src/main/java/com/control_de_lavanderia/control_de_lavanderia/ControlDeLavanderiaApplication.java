package com.control_de_lavanderia.control_de_lavanderia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ControlDeLavanderiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlDeLavanderiaApplication.class, args);
	}

}
