package com.ztrios.opd_appointment_service.repository;

import com.ztrios.opd_appointment_service.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    boolean existsByDoctorIdAndScheduleId(UUID doctorId, UUID scheduleId);
    Optional<AppointmentEntity> findBySerialNo(String serialNo);

}
