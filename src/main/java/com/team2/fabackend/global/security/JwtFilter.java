package com.team2.fabackend.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    /**
     * 들어오는 요청을 필터링하여 JWT 토큰을 검증하고 보안 컨텍스트에 인증 정보를 설정합니다.
     *
     * @param request     HttpServletRequest 객체.
     * @param response    HttpServletResponse 객체.
     * @param filterChain FilterChain 객체.
     * @throws ServletException 서블릿 관련 오류가 발생한 경우.
     * @throws IOException      I/O 오류가 발생한 경우.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            try {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청의 Authorization 헤더에서 JWT 토큰을 추출합니다.
     *
     * @param request HttpServletRequest 객체.
     * @return 추출된 JWT 토큰, 찾을 수 없거나 유효하지 않은 경우 null.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
