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
     * 비밀번호 확인 토큰 저장을 위한 Redis 키를 생성합니다.
     *
     * @param userId 사용자의 ID.
     * @return Redis 키 문자열.
     */
    private String getPasswordKey(Long userId) {
        return "pwd_verify:" + userId;
    }

    /**
     * 비밀번호 확인 토큰을 10분 만료 시간으로 Redis에 저장합니다.
     *
     * @param userId 사용자의 ID.
     * @param token  확인용 토큰.
     */
    public void saveVerificationToken(Long userId, String token) {
        redisTemplate.opsForValue().set(getPasswordKey(userId), token, Duration.ofMinutes(10));
    }

    /**
     * 제공된 확인 토큰을 Redis에 저장된 토큰과 대조하여 검증합니다.
     *
     * @param userId 사용자의 ID.
     * @param token  검증할 토큰.
     * @throws RuntimeException 토큰이 없거나, 유효하지 않거나, 만료된 경우.
     */
    public void validateVerificationToken(Long userId, String token) {
        String savedToken = redisTemplate.opsForValue().get(getPasswordKey(userId));

        if (savedToken == null || !savedToken.equals(token)) {
            throw new RuntimeException("인증 정보가 없거나 만료되었습니다. 다시 인증해주세요.");
        }
    }

    /**
     * 사용자의 비밀번호 확인 토큰을 Redis에서 삭제합니다.
     *
     * @param userId 사용자의 ID.
     */
    public void deleteVerification(Long userId) {
        redisTemplate.delete(getPasswordKey(userId));
    }
}
