package com.team2.fabackend.global.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final Long userId;

    public JwtAuthenticationToken(Long userId, List<GrantedAuthority> authorities) {
        super(userId, null, authorities);
        this.userId = userId;
    }

}
