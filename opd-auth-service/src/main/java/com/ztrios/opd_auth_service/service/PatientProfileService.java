package com.ztrios.opd_auth_service.service;


import com.ztrios.opd_auth_service.dto.PatientProfileResponse;
import com.ztrios.opd_auth_service.dto.PatientProfileUpdateRequest;
import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.exception.ResourceNotFoundException;
import com.ztrios.opd_auth_service.mapper.PatientProfileMapper;
import com.ztrios.opd_auth_service.repository.PatientProfileRepository;
import com.ztrios.opd_auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientProfileService {


    private final UserRepository userRepository;
    private final PatientProfileRepository profileRepository;


    public PatientProfileResponse getPatientProfile(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PatientProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return PatientProfileMapper.toResponse(user, profile);
    }


    public PatientProfileResponse updatePatientProfile(
            String userId,
            PatientProfileUpdateRequest request
    ) {
        UserEntity user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PatientProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        // Update fields
        user.setFullName(request.fullName());
        user.setPhone(request.phone());

        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(request.gender());
        profile.setBloodGroup(request.bloodGroup());
        profile.setAddress(request.address());

        // Save
        userRepository.save(user);
        profileRepository.save(profile);

        return PatientProfileMapper.toResponse(user, profile);
    }
}
