package com.ztrios.opd_appointment_service.repository;

import com.ztrios.opd_appointment_service.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface appointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    boolean existsByDoctorIdAndScheduleId(UUID doctorId, UUID scheduleId);
    Optional<AppointmentEntity> findByAppointmentSerialNo(String serialNo);

}
