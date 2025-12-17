package com.ztrios.opd_doctor_service.entity;


import com.ztrios.opd_doctor_service.enums.DoctorStatus;
import com.ztrios.opd_doctor_service.enums.Specialization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctors")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class DoctorEntity {

    @Id
    @GeneratedValue
    private UUID id;


    @Column(nullable = false, unique = true)
    private UUID userId; // From Identity Service


    private String degree;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    private Integer experienceYears;

    @Column(nullable = false, unique = true)
    private String licenseNumber;


    private BigDecimal consultationFee;


    @Enumerated(EnumType.STRING)
    private DoctorStatus status;


    @Column(length = 1000)
    private String bio;

    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorScheduleEntity> schedules;
}
