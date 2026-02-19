package com.af.carrsvt;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.repository.CustomerRepository;

@SpringBootApplication
public class CarReservationApplication {

	public static final Logger log = LoggerFactory.getLogger(CarReservationApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(CarReservationApplication.class, args);
	}

}
