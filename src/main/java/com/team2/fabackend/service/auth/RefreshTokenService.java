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

    /**
     * 사용자의 리프레시 토큰을 위한 Redis 키를 생성합니다.
     *
     * @param userId 사용자의 ID.
     * @return Redis 키 문자열.
     */
    private String getKey(Long userId) {
        return "refresh_token:" + userId;
    }

    /**
     * 지정된 만료 시간으로 사용자의 리프레시 토큰을 저장합니다.
     *
     * @param userId       사용자의 ID.
     * @param refreshToken 리프레시 토큰 문자열.
     * @param ttl          토큰의 유효 기간.
     */
    public void saveRefreshToken(Long userId, String refreshToken, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(getKey(userId), refreshToken, ttl);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 제공된 리프레시 토큰을 Redis에 저장된 토큰과 대조하여 검증합니다.
     *
     * @param userId 사용자의 ID.
     * @param token  검증할 토큰.
     * @throws CustomException 토큰이 없거나 일치하지 않는 경우.
     */
    public void validateRefreshToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getKey(userId));

        if (savedToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!savedToken.equals(token)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }
    }

    /**
     * 사용자의 리프레시 토큰을 Redis에서 삭제합니다.
     *
     * @param userId 사용자의 ID.
     */
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(getKey(userId));
    }
}
