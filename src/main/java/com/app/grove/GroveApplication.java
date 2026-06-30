package com.app.grove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GroveApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroveApplication.class, args);
	}

}
