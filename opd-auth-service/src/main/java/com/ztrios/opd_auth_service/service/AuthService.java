package com.ztrios.opd_auth_service.service;


import com.ztrios.opd_auth_service.dto.AuthResponse;
import com.ztrios.opd_auth_service.dto.RegisterPatientRequest;
import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;


    public AuthResponse register(RegisterPatientRequest request){

//        if (userRepository.findByEmail(request.email()).isPresent()) {
//            throw new RuntimeException("Email already registered: " + request.email());
//        }

        // Create User
        UserEntity user = new UserEntity();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(request.password());


//        // Create Profile
//        PatientProfileEntity profile = new PatientProfileEntity();
//        profile.setUser(user);
//        profile.setDateOfBirth(request.dateOfBirth());
//        profile.setGender(request.gender());
//        profile.setBloodGroup(request.bloodGroup());
//        profile.setAddress(request.address());

//        user.setPatientProfile(profile);

        userRepository.save(user);
        //patientProfileRepository.save(profile);

        return new AuthResponse("", "Patient registered successfully");

    }

}
