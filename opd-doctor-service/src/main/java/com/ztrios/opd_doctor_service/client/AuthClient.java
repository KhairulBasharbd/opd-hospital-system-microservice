package com.ztrios.opd_doctor_service.client;


import com.ztrios.opd_doctor_service.dto.UserCreationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthClient {

    @PostMapping("/api/users")
    void createUser(@RequestBody UserCreationRequest request);

    @GetMapping("/api/users/admin/exists")
    boolean adminExists();
}
