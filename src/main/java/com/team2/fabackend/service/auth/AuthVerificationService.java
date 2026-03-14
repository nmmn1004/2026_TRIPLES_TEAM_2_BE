package com.team2.fabackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthVerificationService {
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Generates the Redis key for storing password verification tokens.
     *
     * @param userId The ID of the user.
     * @return The string key for Redis.
     */
    private String getPasswordKey(Long userId) {
        return "pwd_verify:" + userId;
    }

    /**
     * Saves a password verification token in Redis with a 10-minute TTL.
     *
     * @param userId The ID of the user.
     * @param token  The verification token.
     */
    public void saveVerificationToken(Long userId, String token) {
        redisTemplate.opsForValue().set(getPasswordKey(userId), token, Duration.ofMinutes(10));
    }

    /**
     * Validates the provided verification token against the one stored in Redis.
     *
     * @param userId The ID of the user.
     * @param token  The token to validate.
     * @throws RuntimeException If the token is missing, invalid, or expired.
     */
    public void validateVerificationToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getPasswordKey(userId));

        if (savedToken == null || !savedToken.equals(token)) {
            throw new RuntimeException("인증 정보가 없거나 만료되었습니다. 다시 인증해주세요.");
        }
    }

    /**
     * Deletes the password verification token for a user from Redis.
     *
     * @param userId The ID of the user.
     */
    public void deleteVerification(Long userId) {
        redisTemplate.delete(getPasswordKey(userId));
    }
}
