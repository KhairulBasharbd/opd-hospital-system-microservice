package com.ztrios.opd_notification_service.service.impl;

import com.ztrios.opd_notification_service.dto.NotificationRequest;
import com.ztrios.opd_notification_service.dto.event.AppointmentConfirmedEvent;
import com.ztrios.opd_notification_service.dto.event.AppointmentCreatedEvent;
import com.ztrios.opd_notification_service.enums.NotificationType;
import com.ztrios.opd_notification_service.service.MessageFormatterService;
import org.springframework.stereotype.Service;

@Service
public class MessageFormatterServiceImpl implements MessageFormatterService {

    @Override
    public NotificationRequest formatAppointmentCreated(AppointmentCreatedEvent event) {

        String subject = "Appointment Booked – Payment Pending";

        String message = """
                Dear %s,

                Your appointment with Dr. %s on %s is booked successfully.

                ⏰ Time: %s - %s
                💰 Fee: %s

                Please complete payment using the link below:
                %s

                Thank you,
                OPD Hospital
                """.formatted(
                event.patient().fullName(),
                event.doctorName(),
                event.appointmentDate(),
                event.startTime(),
                event.endTime(),
                event.consultationFee(),
                event.paymentUrl()
        );

        return new NotificationRequest(
                NotificationType.APPOINTMENT_CREATED,
                event.patient().email(),
                event.patient().phone(),
                subject,
                message
        );
    }

    @Override
    public NotificationRequest formatAppointmentConfirmed(AppointmentConfirmedEvent event) {

        String subject = "Appointment Confirmed";

        String message = """
                Dear %s,

                ✅ Your appointment has been confirmed.

                Doctor: Dr. %s
                Date: %s
                Serial No: %d

                Please arrive 10 minutes earlier.

                Regards,
                OPD Hospital
                """.formatted(
                event.patient().fullName(),
                event.doctorName(),
                event.appointmentDate(),
                event.serialNo()
        );

        return new NotificationRequest(
                NotificationType.APPOINTMENT_CONFIRMED,
                event.patient().email(),
                event.patient().phone(),
                subject,
                message
        );
    }
}

