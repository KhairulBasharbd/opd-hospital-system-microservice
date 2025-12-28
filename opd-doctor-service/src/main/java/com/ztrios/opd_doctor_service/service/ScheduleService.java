package com.ztrios.opd_doctor_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ztrios.opd_doctor_service.repository.*;
import com.ztrios.opd_doctor_service.dto.*;
import com.ztrios.opd_doctor_service.entity.*;
import com.ztrios.opd_doctor_service.exception.*;



import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {


    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;


    public DoctorScheduleResponse createSchedule(UUID doctorId, CreateDoctorScheduleRequest request) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        //checkAuthorizationForDoctor(doctor);
        DoctorScheduleEntity schedule = new DoctorScheduleEntity();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setMaxPatients(request.maxPatients());
        schedule = scheduleRepository.save(schedule);

        return mapToScheduleResponse(schedule);
    }


    public List<DoctorScheduleResponse> getSchedulesByDoctorId(UUID doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToScheduleResponse)
                .collect(Collectors.toList());
    }

    public DoctorScheduleResponse getScheduleById(UUID scheduleId) {
        DoctorScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        return mapToScheduleResponse(schedule);
    }

    public DoctorScheduleResponse updateSchedule(UUID scheduleId, CreateDoctorScheduleRequest request) {
        DoctorScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        //checkAuthorizationForDoctor(schedule.getDoctor());
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setMaxPatients(request.maxPatients());
        schedule = scheduleRepository.save(schedule);


        return mapToScheduleResponse(schedule);
    }

    public void deleteSchedule(UUID scheduleId) {
        DoctorScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        //checkAuthorizationForDoctor(schedule.getDoctor());
        scheduleRepository.delete(schedule);
    }

    private DoctorScheduleResponse mapToScheduleResponse(DoctorScheduleEntity schedule) {
        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMaxPatients(),
                schedule.getAppointedPatients()
        );
    }

}
