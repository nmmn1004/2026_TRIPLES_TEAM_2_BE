# 1단계: 빌드 (모든 아키텍처를 지원하는 Debian 기반 Gradle 이미지 사용)
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# 종속성 캐싱
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# 소스 복사 및 빌드
COPY src src
RUN gradle clean build -x test --no-daemon

# 2단계: 실행 (JRE 사용 - 실행 이미지는 용량이 작은 Alpine 유지)
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# 보안을 위해 비루트 사용자 추가
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# 빌드 결과물(jar) 복사 시 소유권 보장
COPY --chown=spring:spring --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]