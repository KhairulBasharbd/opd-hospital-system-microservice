package com.ztrios.opd_notification_service.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsClient {

    public void sendSms(String phone, String message) {
        // Replace with Twilio / Nexmo later
        log.info("""
                📱 Sending SMS
                To: {}
                Message:
                {}
                """, phone, message);
    }
}
