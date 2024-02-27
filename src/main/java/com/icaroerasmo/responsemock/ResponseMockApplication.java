package com.icaroerasmo.responsemock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ResponseMockApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResponseMockApplication.class, args);
	}

}
