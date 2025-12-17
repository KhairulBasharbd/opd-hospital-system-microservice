package com.ztrios.opd_doctor_service.repository;

import com.ztrios.opd_doctor_service.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {
}
