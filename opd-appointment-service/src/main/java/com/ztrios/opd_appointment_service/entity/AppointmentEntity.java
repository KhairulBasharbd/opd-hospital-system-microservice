package com.ztrios.opd_appointment_service.entity;


import com.ztrios.opd_appointment_service.enums.AppointmentStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "appointments", uniqueConstraints = {@UniqueConstraint(columnNames = {"patient_id","doctor_id","Schedule_id","serial_no"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @Column(name = "patient_id", nullable = false)
    private UUID patientUserId;


    @Column(name = "doctor_id",nullable = false)
    private UUID doctorId;


    @Column(name = "schedule_id",nullable = false)
    private UUID scheduleId;


    @Column(name = "appointment_date",nullable = false)
    private LocalDate appointmentDate;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;


    @Column(name = "serial_no",nullable = true)
    private Integer serialNo ;


    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false )
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    private Instant updatedAt;
}
