package com.team2.fabackend.service.auth;

import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    private String getKey(Long userId) {
        return "refresh_token:" + userId;
    }

    public void saveRefreshToken(Long userId, String refreshToken, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(getKey(userId), refreshToken, ttl);
        } catch (Exception e) {
            // Redis 연결 오류 등 인프라 에러 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void validateRefreshToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getKey(userId));

        if (savedToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!savedToken.equals(token)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(getKey(userId));
    }
}
