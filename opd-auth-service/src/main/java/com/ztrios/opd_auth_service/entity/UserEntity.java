package com.ztrios.opd_auth_service.entity;

import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import com.ztrios.opd_auth_service.enums.AuthProvider;
import com.ztrios.opd_auth_service.enums.Role;
import com.ztrios.opd_auth_service.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = true)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String passwordHash;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AuthProvider provider;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(nullable = true, updatable = true)
    private Instant updatedAt ;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private PatientProfileEntity patientProfile;

}

