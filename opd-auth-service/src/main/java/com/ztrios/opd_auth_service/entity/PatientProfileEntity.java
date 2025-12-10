package com.ztrios.opd_auth_service.entity;

import com.ztrios.opd_auth_service.enums.BloodGroup;
import com.ztrios.opd_auth_service.enums.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient_profiles")
@Builder
@Data
@NoArgsConstructor
public class PatientProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    private LocalDate dateOfBirth;
    private Gender gender;
    private BloodGroup bloodGroup;
    private String address;
}
