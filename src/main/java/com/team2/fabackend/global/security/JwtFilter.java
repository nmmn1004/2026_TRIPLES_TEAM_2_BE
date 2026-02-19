package com.team2.fabackend.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        log.info("🔍 URI: {}, Token: {}", request.getRequestURI(),
                token != null ? "있음" : "없음");

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            try {
                Long userId = jwtProvider.getUserIdFromToken(token);
                log.info("🔍 UserId 추출 성공: {}", userId);

                Authentication authentication = jwtProvider.getAuthentication(token);
                log.info("🔍 Authentication 생성:");
                log.info("   - Principal: {} (type: {})",
                        authentication.getPrincipal(),
                        authentication.getPrincipal().getClass().getSimpleName());
                log.info("   - Authorities: {}", authentication.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ SecurityContext 설정 완료");

            } catch (Exception e) {
                log.error("❌ Authentication 생성 실패: {}", e.getMessage());
            }
        } else {
            log.warn("❌ 토큰 없음 또는 유효하지 않음");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("🔍 Authorization Header: '{}'", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
