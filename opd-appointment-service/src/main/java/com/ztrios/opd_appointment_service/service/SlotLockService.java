package com.ztrios.opd_appointment_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SlotLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean acquireBookingLock(UUID doctorId, UUID scheduleId, LocalDate date) {

        String key = "lock:appointment:" + doctorId + ":" + scheduleId + ":" + date;
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue()
                        .setIfAbsent(key, "LOCKED", Duration.ofSeconds(8))
        );
    }

    public void releaseBookingLock(UUID doctorId, UUID scheduleId, LocalDate date) {

        String key = "lock:appointment:" + doctorId + ":" + scheduleId + ":" + date;
        redisTemplate.delete(key);
    }
}
