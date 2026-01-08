package com.ztrios.opd_appointment_service.service;

import com.ztrios.opd_appointment_service.client.BillingClient;
import com.ztrios.opd_appointment_service.client.DoctorClient;
import com.ztrios.opd_appointment_service.dto.*;
import com.ztrios.opd_appointment_service.entity.AppointmentEntity;
import com.ztrios.opd_appointment_service.enums.AppointmentStatus;
import com.ztrios.opd_appointment_service.exception.custom.AppointmentNotFoundException;
import com.ztrios.opd_appointment_service.exception.custom.DuplicateAppointmentException;
import com.ztrios.opd_appointment_service.exception.custom.SlotNotAvailableException;
import com.ztrios.opd_appointment_service.kafka.producer.AppointmentEventProducer;
import com.ztrios.opd_appointment_service.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorClient doctorClient;
    private final BillingClient billingClient;
    private final SlotLockService lockService;
    private final AppointmentEventProducer eventProducer;


    public AppointmentResponse bookAppointment(UUID patientId, BookAppointmentRequest request) {

        UUID doctorId = request.doctorId();
        UUID scheduleId = request.scheduleId();

        log.info("In Service PatientID {}, and DoctorId {}, Date {}, scheduleId {}", patientId, doctorId, request.date(), scheduleId);


//        Checking that does this patient already appointed a schedule of this doctor on a specific date ??
        if (appointmentRepository.existsByPatientUserIdAndDoctorIdAndScheduleIdAndAppointmentDate( patientId, doctorId, scheduleId, request.date())) {

            throw new DuplicateAppointmentException("An appointment with the same patient, doctor, schedule, and date already exists.");
        }

        Integer lastSerialNo = appointmentRepository.findMaxSerialNoByAppointmentDateAndDoctorIdAndScheduleId(request.date(),doctorId, scheduleId, AppointmentStatus.CONFIRMED);
        Integer newSerialNo = lastSerialNo + 1;

        if (!doctorClient.isScheduleAvailable(doctorId, scheduleId, newSerialNo , request.date())) {
            throw new SlotNotAvailableException("Doctor Schedule isn't available for Booking appointment!");
        }


//        if (!lockService.acquireBookingLock(doctorId, scheduleId, request.date())) {
//            throw new SlotNotAvailableException("Booking in progress. Try again.!!");
//        }



        AppointmentEntity appointment = appointmentRepository.save(
                AppointmentEntity.builder()
                        .patientUserId(patientId)
                        .doctorId(doctorId)
                        .scheduleId(scheduleId)
                        .appointmentDate(request.date())
                        .status(AppointmentStatus.PENDING_PAYMENT)
                        .createdAt(Instant.now())
                        .build()
        );


        BillingServiceResponse billing = billingClient.createInvoice(
                new BillingServiceRequest(appointment.getId(), patientId, doctorId, appointment.getScheduleId(), appointment.getAppointmentDate())
        );

        //BillingServiceResponse billing = new BillingServiceResponse("INV-2025-001","example-pay-link.com/INV-2025-001");



        //  Async notification event
        eventProducer.publishAppointmentCreated(
                new AppointmentCreatedEvent(
                        appointment.getId(),
                        patientId
//                        doctorId,
//                        appointment.getAppointmentDate()
                )
        );

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStatus(),
                billing.paymentLink()
        );
    }


    public Integer countAppointments(UUID doctorId, UUID scheduleId, LocalDate date, AppointmentStatus status){
       return  appointmentRepository.findMaxSerialNoByAppointmentDateAndDoctorIdAndScheduleId(date, doctorId, scheduleId, AppointmentStatus.CONFIRMED);

    }


    // Called after consuming payment confirmed event generated from billing service
    public void confirmAppointment(UUID appointmentId) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));


        appointment.setStatus(AppointmentStatus.CONFIRMED);

        //Integer lastSerialNo = appointmentRepository.findMaxSerialNoByAppointmentDateAndDoctorIdAndScheduleId(appointment.getAppointmentDate(),appointment.getDoctorId(), appointment.getScheduleId(), AppointmentStatus.CONFIRMED);

        Integer lastSerialNo = countAppointments(appointment.getDoctorId(), appointment.getScheduleId(), appointment.getAppointmentDate(), AppointmentStatus.CONFIRMED);

        Integer newSerialNo = lastSerialNo + 1;

        appointment.setSerialNo(newSerialNo);

        appointmentRepository.save(appointment);

        //  Async notification event
        eventProducer.publishAppointmentConfirmed(
                new AppointmentConfirmedEvent(
                        appointment.getId(),
                        appointment.getDoctorId(),
                        appointment.getScheduleId(),
                        appointment.getAppointmentDate(),
                        appointment.getSerialNo()
                )
        );

    }
}




