package com.team2.fabackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthVerificationService {
    private final RedisTemplate<String, String> redisTemplate;

    private String getPasswordKey(Long userId) {
        return "pwd_verify:" + userId;
    }

    public void saveVerificationToken(Long userId, String token) {
        redisTemplate.opsForValue().set(getPasswordKey(userId), token, Duration.ofMinutes(10));
    }

    public void validateVerificationToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getPasswordKey(userId));

        if (savedToken == null || !savedToken.equals(token)) {
            throw new RuntimeException("인증 정보가 없거나 만료되었습니다. 다시 인증해주세요.");
        }
    }

    public void deleteVerification(Long userId) {
        redisTemplate.delete(getPasswordKey(userId));
    }
}
