package com.ztrios.opd_appointment_service.service;

import com.ztrios.opd_appointment_service.client.BillingClient;
import com.ztrios.opd_appointment_service.client.DoctorClient;
import com.ztrios.opd_appointment_service.dto.*;
import com.ztrios.opd_appointment_service.entity.AppointmentEntity;
import com.ztrios.opd_appointment_service.enums.AppointmentStatus;
import com.ztrios.opd_appointment_service.exception.custom.AppointmentNotFoundException;
import com.ztrios.opd_appointment_service.exception.custom.SlotNotAvailableException;
import com.ztrios.opd_appointment_service.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppointmentService {

    private final AppointmentRepository repository;
    private final DoctorClient doctorClient;
    private final BillingClient billingClient;
    private final SlotLockService lockService;
    //private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppointmentEventProducer eventProducer;


    public AppointmentResponse bookAppointment(UUID patientId,
                                               BookAppointmentRequest request) {

        UUID doctorId = request.doctorId();
        UUID scheduleId = request.scheduleId();

        log.info("In Service ID {}, and DoctorId {}, Date {}", patientId, request.doctorId(), request.date());

        if (!doctorClient.isScheduleAvailable(doctorId, scheduleId, request.date())) {
            throw new SlotNotAvailableException("Schedule isn't available for Booking appointment!");
        }


        if (!lockService.lockSlot(doctorId, scheduleId)) {
            throw new SlotNotAvailableException("Slot is already locked!");
        }


        AppointmentEntity appointment = repository.save(
                AppointmentEntity.builder()
                        .patientUserId(patientId)
                        .doctorId(doctorId)
                        .scheduleId(scheduleId)
                        .appointmentDate(request.date())
                        .status(AppointmentStatus.PENDING_PAYMENT)
                        .serialNo(Long.getLong("1"))
                        .createdAt(Instant.now())
                        .build()
        );


        BillingServiceResponse billing = billingClient.createInvoice(
                new BillingServiceRequest(appointment.getId(), patientId, doctorId)
        );

        //BillingServiceResponse billing = new BillingServiceResponse("INV-2025-001","example-pay-link.com/INV-2025-001");



//        kafkaTemplate.send("APPOINTMENT_CREATED",
//                new AppointmentCreatedEvent(appointment.getId(), patientId));

        // 5. Async notification event
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
                appointment.getSerialNo(),
                billing.paymentLink()
        );
    }


    public void confirmAppointment(UUID appointmentId) {
        AppointmentEntity appt = repository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));


        appt.setStatus(AppointmentStatus.CONFIRMED);


//        kafkaTemplate.send("APPOINTMENT_CONFIRMED",
//                new AppointmentConfirmedEvent(appointmentId));

        eventProducer.publishAppointmentConfirmed(
                new AppointmentConfirmedEvent(
                        appt.getId()
                )
        );

    }
}




