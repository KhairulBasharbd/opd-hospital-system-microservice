package com.ztrios.opd_doctor_service.service;


import com.ztrios.opd_doctor_service.exception.custom.DoctorNotFoundException;
import com.ztrios.opd_doctor_service.exception.custom.UnauthorizedException;
import com.ztrios.opd_doctor_service.repository.*;
import com.ztrios.opd_doctor_service.dto.*;
import com.ztrios.opd_doctor_service.enums.*;
import com.ztrios.opd_doctor_service.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ScheduleService scheduleService;

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

        log.warn("Doctor is saved at : {}", doctor.getCreatedAt());

        DoctorEntity savedDoctor = doctorRepository.save(doctor);

        return mapToDoctorResponse(savedDoctor);
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

    public List<DoctorAvailabilityResponse> getAvailableDoctors(DayOfWeek dayOfWeek, Specialization specialization){


        return doctorRepository
                .findAvailableDoctors(dayOfWeek, specialization)
                .stream()
                .map(this::mapToDoctorAvailabilityResponse)
                .toList();
    }



    public DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest request) {
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





    private DoctorResponse mapToDoctorResponse(DoctorEntity doctor) {

//        List<DoctorScheduleResponse> schedules = doctor.getSchedules() != null ?
//                doctor.getSchedules().stream().map(this::mapToScheduleResponse).collect(Collectors.toList()) : List.of();
        return new DoctorResponse(
                doctor.getId(),
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

    private DoctorAvailabilityResponse mapToDoctorAvailabilityResponse(DoctorEntity doctor) {

        return new DoctorAvailabilityResponse(
                doctor.getId(),
                doctor.getDegree(),
                doctor.getSpecialization(),
                doctor.getExperienceYears(),
                doctor.getLicenseNumber(),
                doctor.getConsultationFee(),
                doctor.getStatus(),
                doctor.getBio()
//                doctor.getSchedules().stream().map(this::mapToScheduleResponse).collect(Collectors.toList())

        );
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
