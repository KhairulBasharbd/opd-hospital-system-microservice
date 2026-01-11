package com.ztrios.opd_notification_service.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailClient {

    public void sendEmail(String to, String subject, String body) {
        // Replace with SMTP / SES / SendGrid later
        log.info("""
                📧 Sending EMAIL
                To: {}
                Subject: {}
                Body:
                {}
                """, to, subject, body);
    }
}
