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
        String serverUrl = "https://dontory.duckdns.org";

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityScheme confirmTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Password-Confirm-Token");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName)
                .addList(confirmTokenSchemeName);

        return new OpenAPI()
                .info(new Info()
                        .title("Team2 Backend API")
                        .description("""
                                <h2>project-name의 백엔드 API swagger</h2>
                                </hr>
                                <h4>토큰 발급 방법</h4>
                                <ol>
                                    <li>로그인 요청</li>
                                    <li>로그인 시도</li>
                                    <li>토큰 복사</li>
                                    <li>Authorize 선택 후 토큰으로 인증</li>
                                </ol>
                                """)
                        .version("v1.0.0")
                )
                .servers(List.of(
                        new Server().url("https://dontory.duckdns.org").description("운영 서버"),
                        new Server().url("http://localhost:8080").description("로컬 테스트")
                ))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, securityScheme)
                        .addSecuritySchemes(confirmTokenSchemeName, confirmTokenScheme)
                );
    }
}
