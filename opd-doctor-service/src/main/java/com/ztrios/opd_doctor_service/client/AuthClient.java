package com.ztrios.opd_doctor_service.client;


import com.ztrios.opd_doctor_service.dto.UserCreationRequest;
import com.ztrios.opd_doctor_service.dto.UserCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "opd-auth-service")
public interface AuthClient {

    @PostMapping("/register/users")
    UserCreationResponse createUser(@RequestBody UserCreationRequest request);

    @GetMapping("/admin/exists")
    boolean adminExists();
}
