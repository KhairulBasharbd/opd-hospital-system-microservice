package com.ztrios.opd_auth_service.controller;


import com.ztrios.opd_auth_service.dto.*;
import com.ztrios.opd_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {



//    private final RegisterPatientUseCase registerPatientUseCase;
//    private final LoginUseCase loginUseCase;
    private final AuthService authService;

    @PostMapping("/register/patient")
    public ResponseEntity<RegisterPatientResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {

        RegisterPatientResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/register/users")
    public ResponseEntity<RegisterPatientResponse> registerPatient(@Valid @RequestBody UserCreationRequest request) {

        RegisterPatientResponse response = authService.userRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/exists")
    public boolean adminExists() {
        return authService.adminExists();
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        var result = authService.login(req);
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), "Bearer", result.expiresInSeconds(), result.email()));
    }


}
