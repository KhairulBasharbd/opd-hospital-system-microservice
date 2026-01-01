package com.ztrios.opd_doctor_service.service;


import com.ztrios.opd_doctor_service.entity.DoctorEntity;
import com.ztrios.opd_doctor_service.entity.DoctorScheduleEntity;
import com.ztrios.opd_doctor_service.enums.DoctorStatus;
import com.ztrios.opd_doctor_service.exception.custom.DoctorNotFoundException;
import com.ztrios.opd_doctor_service.exception.custom.ScheduleNotFoundException;
import com.ztrios.opd_doctor_service.repository.DoctorRepository;
import com.ztrios.opd_doctor_service.repository.DoctorScheduleRepository;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;

    public boolean isScheduleAvailable(
            UUID doctorId,
            UUID scheduleId,
            Integer lastSerialNo,
            LocalDate appointmentDate
    ) {


        // 1. Doctor existence & status check
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found!! Id : " + doctorId));

        if (doctor.getStatus() != DoctorStatus.ACTIVE) {
            return false;
        }

        // 2. Schedule belongs to doctor
        DoctorScheduleEntity schedule = scheduleRepository
                .findByIdAndDoctorId(scheduleId, doctorId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found!! Id : " + scheduleId));

        // 3. Day-of-week validation
        if (!schedule.getDayOfWeek().equals(appointmentDate.getDayOfWeek())) {
            return false;
        }

        // 4. Capacity validation
        return lastSerialNo <= schedule.getMaxPatients();
    }
}
