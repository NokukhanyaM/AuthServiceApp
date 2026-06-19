package com.example.AuthServiceApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients //Enables the microservices to communicate
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
