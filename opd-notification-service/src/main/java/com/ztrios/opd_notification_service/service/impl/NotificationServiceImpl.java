package com.ztrios.opd_notification_service.service.impl;


import com.ztrios.opd_notification_service.client.EmailClient;
import com.ztrios.opd_notification_service.client.SmsClient;
import com.ztrios.opd_notification_service.dto.NotificationRequest;
import com.ztrios.opd_notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailClient emailClient;
    private final SmsClient smsClient;

    @Override
    public void send(NotificationRequest request) {

        if (request.email() != null) {
            emailClient.sendEmail(
                    request.email(),
                    request.subject(),
                    request.message()
            );
        }

        if (request.phone() != null) {
            smsClient.sendSms(
                    request.phone(),
                    request.message()
            );
        }
    }
}
