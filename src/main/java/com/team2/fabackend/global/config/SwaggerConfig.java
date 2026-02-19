package com.team2.fabackend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";
        String confirmTokenSchemeName = "Confirm Token";

        // 1. JWT Bearer 설정
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // 2. Confirm Token 설정 (주석 해제 및 이름 확정)
        SecurityScheme confirmTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Password-Confirm-Token");

        // 글로벌 보안 요구사항 (기본적으로 모든 API에 JWT 적용)
        SecurityRequirement jwtOnly = new SecurityRequirement()
                .addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, jwtScheme)
                .addSecuritySchemes(confirmTokenSchemeName, confirmTokenScheme);

        return new OpenAPI()
                .info(new Info()
                        .title("Team2 Backend API")
                        .description("""
                                <h2>백엔드 API Swagger</h2>
                                <h4>인증 방법</h4>
                                <ol>
                                    <li>로그인 → <b>accessToken</b> 복사</li>
                                    <li><b>Authorize</b> 버튼 클릭 → <b>JWT</b> 항목에 토큰 붙여넣기</li>
                                    <li>비밀번호 변경 등 2차 인증 필요 시 <b>Confirm Token</b> 항목 사용</li>
                                </ol>
                                """)
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("https://dontory.duckdns.org").description("운영 서버"),
                        new Server().url("http://localhost:8080").description("로컬 테스트")
                ))
                .addSecurityItem(jwtOnly)
                .components(components);
    }
}