package com.team2.fabackend.global.security;


import com.team2.fabackend.global.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-token-validity-in-milliseconds}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidity;

    private SecretKey secretKey; // SecretKey 객체로 변환

    @PostConstruct
    private void init() {
        if (secretKeyString.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters");
        }
        // SecretKey 객체 생성 (HS256)
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createAccessToken(Long userId, UserType userType) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", userType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey) // SecretKey 사용
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey) // SecretKey 사용
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser() // parserBuilder() -> parser()
                    .verifyWith(secretKey) // setSigningKey() -> verifyWith()
                    .build()
                    .parseSignedClaims(token); // parseClaimsJws() -> parseSignedClaims()
            return true;
        } catch (Exception e) {
            // 상세 로그를 남기면 디버깅에 도움이 됩니다.
            return false;
        }
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // getBody() -> getPayload()

        return Long.parseLong(claims.getSubject());
    }
}
