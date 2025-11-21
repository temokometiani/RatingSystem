package com.ratingsystem.service.utilityServices;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redis;

    // Store [email → code]
    public void saveConfirmationCode(String email, String code) {
        redis.opsForValue().set(email, code, 24, TimeUnit.HOURS);
    }

    public String getConfirmationCode(String email) {
        return redis.opsForValue().get(email);
    }

    public void deleteConfirmationCode(String email) {
        redis.delete(email);
    }

    // Reverse lookup: find email by code
    public String getEmailByResetCode(String code) {
        Set<String> keys = redis.keys("*");
        if (keys == null) {
            return null;
        }

        for (String key : keys) {
            String stored = redis.opsForValue().get(key);
            if (stored != null && stored.equals(code)) {
                return key; // key = email
            }
        }
        return null;
    }
}
