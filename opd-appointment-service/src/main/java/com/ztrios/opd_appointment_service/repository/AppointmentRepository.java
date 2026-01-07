package com.ztrios.opd_appointment_service.repository;

import com.ztrios.opd_appointment_service.entity.AppointmentEntity;
import com.ztrios.opd_appointment_service.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    boolean existsByDoctorIdAndScheduleId(UUID doctorId, UUID scheduleId);
    Optional<AppointmentEntity> findBySerialNo(String serialNo);

    boolean existsByPatientUserIdAndDoctorIdAndScheduleIdAndAppointmentDate(UUID patientUserId, UUID doctorId, UUID scheduleId, LocalDate appointmentDate);

    @Query("SELECT COALESCE(MAX(a.serialNo), 0) " +
            "FROM AppointmentEntity a " +
            "WHERE a.appointmentDate = :date " +
            "AND a.doctorId = :doctorId " +
            "AND a.scheduleId = :scheduleId " +
            "AND a.status = :status")
    Integer findMaxSerialNoByAppointmentDateAndDoctorIdAndScheduleId(
            @Param("date") LocalDate date,
            @Param("doctorId") UUID doctorId,
            @Param("scheduleId") UUID scheduleId,
            @Param ("status") AppointmentStatus status
    );
}
