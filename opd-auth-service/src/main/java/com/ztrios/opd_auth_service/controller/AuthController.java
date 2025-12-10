package com.ztrios.opd_auth_service.controller;


import com.ztrios.opd_auth_service.dto.AuthResponse;
import com.ztrios.opd_auth_service.dto.RegisterPatientRequest;
import com.ztrios.opd_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {



//    private final RegisterPatientUseCase registerPatientUseCase;
//    private final LoginUseCase loginUseCase;
    private final AuthService authService;

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        //AuthResponse response = registerPatientUseCase.execute(request);


        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        AuthResponse response = loginUseCase.execute(request);
//        return ResponseEntity.ok(response);
//    }
}
