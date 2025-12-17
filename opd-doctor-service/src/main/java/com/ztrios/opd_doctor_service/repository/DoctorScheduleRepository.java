package com.ztrios.opd_doctor_service.repository;


import com.ztrios.opd_doctor_service.entity.DoctorEntity;
import com.ztrios.opd_doctor_service.entity.DoctorScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, UUID> {

    List<DoctorScheduleEntity>    findByDoctorId(UUID doctorId);

}
