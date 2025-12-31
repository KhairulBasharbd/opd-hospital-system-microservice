package com.ztrios.opd_appointment_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlotLockService {


    private final StringRedisTemplate redisTemplate;


    public boolean lockSlot(UUID doctorId, UUID scheduleId) {
        String key = "slot:" + doctorId + ":" + scheduleId;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "LOCKED", Duration.ofMinutes(4));
        return Boolean.TRUE.equals(success);
    }


    public void releaseSlot(UUID doctorId, UUID scheduleId) {
        redisTemplate.delete("slot:" + doctorId + ":" + scheduleId);
    }
}
