package com.ztrios.opd_appointment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OpdAppointmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpdAppointmentServiceApplication.class, args);
	}

}
