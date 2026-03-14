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
     * Generates the Redis key for a user's refresh token.
     *
     * @param userId The ID of the user.
     * @return The string key for Redis.
     */
    private String getKey(Long userId) {
        return "refresh_token:" + userId;
    }

    /**
     * Saves the refresh token for a user with a specified TTL.
     *
     * @param userId       The ID of the user.
     * @param refreshToken The refresh token string.
     * @param ttl          The duration for which the token is valid.
     */
    public void saveRefreshToken(Long userId, String refreshToken, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(getKey(userId), refreshToken, ttl);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validates the provided refresh token against the one stored in Redis.
     *
     * @param userId The ID of the user.
     * @param token  The token to validate.
     * @throws CustomException If the token is missing or doesn't match.
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
     * Deletes the refresh token for a user from Redis.
     *
     * @param userId The ID of the user.
     */
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(getKey(userId));
    }
}
