package com.ztrios.opd_auth_service.mapper;

import com.ztrios.opd_auth_service.dto.PatientProfileResponse;
import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import com.ztrios.opd_auth_service.entity.UserEntity;

public class PatientProfileMapper {

    public static PatientProfileResponse toResponse(UserEntity user, PatientProfileEntity profile) {
        return new PatientProfileResponse(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                profile.getDateOfBirth(),
                profile.getGender(),
                profile.getBloodGroup(),
                profile.getAddress()
        );
    }
}
