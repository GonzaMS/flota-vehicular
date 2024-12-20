package com.proyecto.flotavehicular_webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FlotavehicularWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlotavehicularWebappApplication.class, args);
    }

}
