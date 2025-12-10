package com.ztrios.opd_auth_service.repository;

import com.ztrios.opd_auth_service.entity.PatientProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientProfileRepository extends JpaRepository<PatientProfileEntity, UUID> {


}
