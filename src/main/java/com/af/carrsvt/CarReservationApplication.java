package com.af.carrsvt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarReservationApplication {

	public static final Logger log = LoggerFactory.getLogger(CarReservationApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(CarReservationApplication.class, args);
	}

}
