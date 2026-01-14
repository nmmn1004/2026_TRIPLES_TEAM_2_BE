############################
# 1단계: Build Stage
############################
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Gradle 캐시 최적화를 위해 의존성 관련 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src src

# 빌드
RUN ./gradlew bootJar -x test --no-daemon


############################
# 2단계: Runtime Stage
############################
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# non-root 사용자 생성 (보안)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# 기본 환경 변수
ENV TZ=Asia/Seoul \
    LANG=C.UTF-8 \
    JAVA_OPTS=""

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트
EXPOSE 8080

# JVM 옵션을 외부에서 주입 가능하게
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]