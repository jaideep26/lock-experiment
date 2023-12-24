package com.sidhucodes.lockexperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LockExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockExperimentApplication.class, args);
	}

}
