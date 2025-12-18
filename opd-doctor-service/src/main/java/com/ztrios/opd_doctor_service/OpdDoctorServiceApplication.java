package com.ztrios.opd_doctor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OpdDoctorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpdDoctorServiceApplication.class, args);
	}

}
