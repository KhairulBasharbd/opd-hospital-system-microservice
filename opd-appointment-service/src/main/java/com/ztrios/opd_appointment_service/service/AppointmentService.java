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
    private final AppointmentEventProducer eventProducer;


    public AppointmentResponse bookAppointment(UUID patientId,
                                               BookAppointmentRequest request) {

        UUID doctorId = request.doctorId();
        UUID scheduleId = request.scheduleId();

        Integer lastSerialNo = repository.findMaxSerialNoByAppointmentDateAndDoctorIdAndScheduleId(request.date(),doctorId, scheduleId);


        log.info("In Service ID {}, and DoctorId {}, Date {}, lastSerialNo {}", patientId, request.doctorId(), request.date(), lastSerialNo);

        Integer newSerialNo = lastSerialNo + 1;

        if (!doctorClient.isScheduleAvailable(doctorId, scheduleId, newSerialNo , request.date())) {
            throw new SlotNotAvailableException("Doctor Schedule isn't available for Booking appointment!");
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
                        .serialNo(lastSerialNo + 1)
                        .createdAt(Instant.now())
                        .build()
        );


//        BillingServiceResponse billing = billingClient.createInvoice(
//                new BillingServiceRequest(appointment.getId(), patientId, doctorId)
//        );

        BillingServiceResponse billing = new BillingServiceResponse("INV-2025-001","example-pay-link.com/INV-2025-001");



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


    // Called after consuming payment confirmed event
    public void confirmAppointment(UUID appointmentId) {
        AppointmentEntity appt = repository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));


        appt.setStatus(AppointmentStatus.CONFIRMED);


        eventProducer.publishAppointmentConfirmed(
                new AppointmentConfirmedEvent(
                        appt.getId()
                )
        );

    }
}




