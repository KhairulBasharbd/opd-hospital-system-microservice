package com.ztrios.opd_auth_service.service;


import com.ztrios.opd_auth_service.config.JwtUtil;
import com.ztrios.opd_auth_service.dto.LoginRequest;
import com.ztrios.opd_auth_service.dto.LoginResult;
import com.ztrios.opd_auth_service.dto.RegisterPatientResponse;
import com.ztrios.opd_auth_service.dto.RegisterPatientRequest;
import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.enums.Role;
import com.ztrios.opd_auth_service.enums.Status;
import com.ztrios.opd_auth_service.exception.custom.AuthenticationFailedException;
import com.ztrios.opd_auth_service.exception.custom.UserAlreadyExistsException;
import com.ztrios.opd_auth_service.repository.PatientProfileRepository;
import com.ztrios.opd_auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    @Transactional
    public RegisterPatientResponse register(RegisterPatientRequest request){


        if (userRepository.existsByEmail(request.email())) {

            log.warn("Email already registered:  {}", request.email());
            throw new UserAlreadyExistsException("Email already registered: " + request.email());
        }

        // Create User
        UserEntity user = new UserEntity();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.PATIENT);
        user.setStatus(Status.ACTIVE);

        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);


        // Create Patient Profile
        PatientProfileEntity patient = new PatientProfileEntity();

        patient.setUser(user);

//        profile.setDateOfBirth(request.dateOfBirth());
//        profile.setGender(request.gender());
//        profile.setBloodGroup(request.bloodGroup());
//        profile.setAddress(request.address());


        user.setPatientProfile(patient);

        userRepository.save(user);
        patientProfileRepository.save(patient);

        return new RegisterPatientResponse("", "Patient registered successfully");

    }



    public LoginResult login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

//         Optional: check account status (e.g. ACTIVE)
//        if (!"ACTIVE".equalsIgnoreCase(user.getStatus().toString())) {
//            throw new AuthenticationFailedException("Account not active");
//        }

        // Optional: check of provider
//        if (user.getProvider() != AuthProvider.LOCAL) {
//            throw new IllegalStateException(
//                    "Please login using " + user.getProvider());
//        }


        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().toString()
        );

        long expires = 900; // 15 minutes (from properties)

        return new LoginResult(token, expires, user.getEmail());
    }

}
