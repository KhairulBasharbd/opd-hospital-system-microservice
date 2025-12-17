package com.ztrios.opd_doctor_service.init;

import com.ztrios.opd_doctor_service.client.AuthClient;
import com.ztrios.opd_doctor_service.dto.UserCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


    @Component
    public class DataInitializer implements CommandLineRunner {

        @Autowired
        private AuthClient authClient;

        @Override
        public void run(String... args) throws Exception {
            if (!authClient.adminExists()) {
                // Create default admin user in auth service
                UserCreationRequest adminRequest = new UserCreationRequest(
                        "admin",
                        "adminpassword", // Hash in auth service
                        "admin@opd.com",
                        "ADMIN"
                );
                authClient.createUser(adminRequest);
                // Optionally, create a default doctor linked to this admin if needed
            }
        }
    }

