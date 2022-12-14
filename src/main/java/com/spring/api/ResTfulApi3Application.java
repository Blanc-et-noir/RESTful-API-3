package com.spring.api;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class ResTfulApi3Application {
	public static void main(String[] args) {
		SpringApplication.run(ResTfulApi3Application.class, args);
	}
}