package com.ztrios.opd_doctor_service.service;


import com.ztrios.opd_doctor_service.dto.CreateDoctorRequest;
import com.ztrios.opd_doctor_service.dto.CreateDoctorScheduleRequest;
import com.ztrios.opd_doctor_service.dto.DoctorResponse;
import com.ztrios.opd_doctor_service.dto.DoctorScheduleResponse;
import com.ztrios.opd_doctor_service.entity.DoctorEntity;
import com.ztrios.opd_doctor_service.entity.DoctorScheduleEntity;
import com.ztrios.opd_doctor_service.exception.DoctorNotFoundException;
import com.ztrios.opd_doctor_service.exception.ScheduleNotFoundException;
import com.ztrios.opd_doctor_service.exception.UnauthorizedException;
import com.ztrios.opd_doctor_service.repository.DoctorRepository;
import com.ztrios.opd_doctor_service.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private DoctorScheduleRepository scheduleRepository;
    //------------------------Doctor Service-----------------------------------------------------------

    public DoctorResponse createDoctor(CreateDoctorRequest request, UUID createdBy) {
        DoctorEntity doctor = new DoctorEntity();
        doctor.setUserId(request.userId());
        doctor.setDegree(request.degree());
        doctor.setSpecialization(request.specialization());
        doctor.setExperienceYears(request.experienceYears());
        doctor.setLicenseNumber(request.licenseNumber());
        doctor.setConsultationFee(request.consultationFee());
        doctor.setStatus(request.status());
        doctor.setBio(request.bio());
        doctor.setCreatedBy(createdBy);
        doctor = doctorRepository.save(doctor);
        return mapToDoctorResponse(doctor);
    }

    public DoctorResponse getDoctorById(UUID id) {
        DoctorEntity doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        return mapToDoctorResponse(doctor);
    }

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse updateDoctor(UUID id, CreateDoctorRequest request) {
        DoctorEntity doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        doctor.setDegree(request.degree());
        doctor.setSpecialization(request.specialization());
        doctor.setExperienceYears(request.experienceYears());
        doctor.setLicenseNumber(request.licenseNumber());
        doctor.setConsultationFee(request.consultationFee());
        doctor.setStatus(request.status());
        doctor.setBio(request.bio());
        doctor = doctorRepository.save(doctor);
        return mapToDoctorResponse(doctor);
    }

    public void deleteDoctor(UUID id) {
        DoctorEntity doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);
    }




    //--------------------------------Schedule Service-------------------------------------------------------

    public DoctorScheduleResponse createSchedule(UUID doctorId, CreateDoctorScheduleRequest request) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        checkAuthorizationForDoctor(doctor);
        DoctorScheduleEntity schedule = new DoctorScheduleEntity();
        schedule.setDoctor(doctor);
        schedule.setDaysOfWeek(request.daysOfWeek());
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
        checkAuthorizationForDoctor(schedule.getDoctor());
        schedule.setDaysOfWeek(request.daysOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setMaxPatients(request.maxPatients());
        schedule = scheduleRepository.save(schedule);
        return mapToScheduleResponse(schedule);
    }

    public void deleteSchedule(UUID scheduleId) {
        DoctorScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        checkAuthorizationForDoctor(schedule.getDoctor());
        scheduleRepository.delete(schedule);
    }




    private DoctorResponse mapToDoctorResponse(DoctorEntity doctor) {
        List<DoctorScheduleResponse> schedules = doctor.getSchedules() != null ?
                doctor.getSchedules().stream().map(this::mapToScheduleResponse).collect(Collectors.toList()) : List.of();
        return new DoctorResponse(
                doctor.getUserId(),
                doctor.getDegree(),
                doctor.getSpecialization(),
                doctor.getExperienceYears(),
                doctor.getLicenseNumber(),
                doctor.getConsultationFee(),
                doctor.getStatus(),
                doctor.getBio(),
                doctor.getCreatedBy(),
                doctor.getCreatedAt()

        );
    }
    private DoctorScheduleResponse mapToScheduleResponse(DoctorScheduleEntity schedule) {
        return new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDaysOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMaxPatients(),
                schedule.getAppointedPatients()
        );
    }


    private void checkAuthorizationForDoctor(DoctorEntity doctor) {
        // Assume auth provides current user ID via SecurityContext
        UUID currentUserId = getCurrentUserId(); // Implement based on your auth, e.g., JWT claims
        boolean isAdmin = isCurrentUserAdmin(); // Implement based on roles
        if (!isAdmin && !doctor.getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized to manage this doctor's schedule");
        }
    }

    // Placeholder methods; implement based on your auth integration
    private UUID getCurrentUserId() {
        // e.g., return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return null; // Replace with actual impl
    }

    private boolean isCurrentUserAdmin() {
        // e.g., return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return false; // Replace with actual impl
    }

}
