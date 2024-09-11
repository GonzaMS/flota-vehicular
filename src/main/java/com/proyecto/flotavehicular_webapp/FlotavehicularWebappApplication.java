package com.proyecto.flotavehicular_webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.proyecto.flotavehicular_webapp.*")
public class FlotavehicularWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlotavehicularWebappApplication.class, args);
	}

}
