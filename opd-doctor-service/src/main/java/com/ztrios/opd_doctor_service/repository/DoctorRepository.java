package com.ztrios.opd_doctor_service.repository;

import com.ztrios.opd_doctor_service.entity.DoctorEntity;
import com.ztrios.opd_doctor_service.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {

    Optional<DoctorEntity> findById(UUID id);

    @Query("""
        SELECT DISTINCT d
        FROM DoctorEntity d
        JOIN d.schedules s
        WHERE d.specialization = :specialization
          AND d.status = 'ACTIVE'
          AND s.DayOfWeek = :dayOfWeek
    """)
    List<DoctorEntity> findAvailableDoctors(DayOfWeek dayOfWeek, Specialization specialization);
}
