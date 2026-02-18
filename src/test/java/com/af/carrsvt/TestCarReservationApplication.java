package com.af.carrsvt;

import org.springframework.boot.SpringApplication;

public class TestCarReservationApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarReservationApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
