package com.ztrios.opd_doctor_service.init;

import com.ztrios.opd_doctor_service.client.AuthClient;
import com.ztrios.opd_doctor_service.dto.UserCreationRequest;
import com.ztrios.opd_doctor_service.dto.UserCreationResponse;
import com.ztrios.opd_doctor_service.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthClient authClient;

    @Override
    public void run(String... args) {
        log.info("🚀 DataInitializer started");

        boolean adminExists = authClient.adminExists();
        log.info("Admin exists: {}", adminExists);

        if (!adminExists) {
            log.info("Creating default ADMIN user");

            UserCreationRequest adminRequest = new UserCreationRequest(
                    "admin",
                    "adminpassword",
                    "admin@opd.com",
                    Role.ADMIN
            );

            authClient.createUser(adminRequest);
            log.info("✅ ADMIN user created successfully");
        } else {
            log.info("ℹ️ ADMIN already exists, skipping creation");
        }
    }
}

