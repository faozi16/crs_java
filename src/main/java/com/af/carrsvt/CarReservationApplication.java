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

	@Bean
	public CommandLineRunner demo(CustomerRepository repo)
	{
		return (args) -> {
			repo.save(new Customer(null,"syafiq","sabilulhaq","syafiq@piat2.edu","","A","","","",""));
			repo.save(new Customer(null,"nabilul","khoir","nabilul@gontor1.edu","","A","","","",""));
			repo.save(new Customer(null,"faiz","kiram","faiz@gontor1.edu","","A","","","",""));

			log.info("CarReservationApplication - demo");
			log.info("Customer found with findAll");
			repo.findAll().forEach(customer -> {
				log.info(customer.toString());
			});
			log.info("");

			Optional<Customer> c = repo.findById(1L);
			log.info("Customer found with findByID(1L)");
			log.info(c.toString());
			log.info("");	
		};
	}

}
