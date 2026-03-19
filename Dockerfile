# 1단계: 빌드 (Multi-arch 지원 Debian 기반 Gradle 이미지)
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Gradle 빌드 성능 최적화 설정
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true"

# 1. 의존성 레이어 캐싱
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# 2. 애플리케이션 빌드
COPY src src
RUN gradle bootJar -x test --no-daemon

# 잠재적 버그 방지: Spring Boot 2.5+ 에서 생성되는 -plain.jar 파일을 미리 삭제하여
# 다음 단계에서 실행 가능한 jar 파일만 안전하게 복사되도록 조치합니다.
RUN rm -f build/libs/*-plain.jar

# 2단계: 실행 (최경량 Alpine 기반 JRE 이미지)
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# 컨테이너 리소스 제한에 맞춰 Java 메모리 최적화
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"

# 보안을 위해 비루트(Non-root) 사용자 설정
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# 빌드 단계에서 생성된 실행 가능한 jar만 복사
COPY --chown=spring:spring --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]