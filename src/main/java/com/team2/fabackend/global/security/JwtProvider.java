package com.team2.fabackend.global.security;

import com.team2.fabackend.global.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-token-validity-in-milliseconds}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidity;

    private SecretKey secretKey;

    /**
     * 의존성 주입 후 비밀 키를 초기화합니다.
     */
    @PostConstruct
    private void init() {
        if (secretKeyString.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 사용자를 위한 액세스 토큰을 생성합니다.
     *
     * @param userId   사용자의 ID.
     * @param userType 사용자의 유형/역할.
     * @return 생성된 액세스 토큰 문자열.
     */
    public String createAccessToken(Long userId, UserType userType) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", userType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 사용자를 위한 리프레시 토큰을 생성합니다.
     *
     * @param userId 사용자의 ID.
     * @return 생성된 리프레시 토큰 문자열.
     */
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey) 
                .compact();
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 토큰.
     * @return 토큰이 유효하면 true, 그렇지 않으면 false.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token 토큰 문자열.
     * @return Long 형식의 사용자 ID.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * 제공된 JWT 토큰을 기반으로 Authentication 객체를 생성합니다.
     *
     * @param token 토큰 문자열.
     * @return Authentication 객체.
     */
    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("role", String.class);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
        );

        return new JwtAuthenticationToken(userId, authorities);
    }
}
