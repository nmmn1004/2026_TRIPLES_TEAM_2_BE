# 1단계: 빌드 (Multi-arch 지원 Debian 기반 Gradle 이미지)
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Gradle 빌드 성능 최적화 설정
# --parallel: 프로젝트 모듈 간 병렬 빌드
# --build-cache: 동일한 입력에 대한 빌드 결과 재사용
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true"

# 1. 의존성 레이어 캐싱
# build.gradle과 settings.gradle만 먼저 복사하여 의존성을 다운로드합니다.
# 소스 코드가 변경되어도 의존성이 바뀌지 않으면 이 레이어는 캐시됩니다.
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# 2. 애플리케이션 빌드
COPY src src
# 테스트를 제외하고 실행 가능한 jar(bootJar)만 생성하여 효율성을 높입니다.
RUN gradle bootJar -x test --no-daemon

# 2단계: 실행 (최경량 Alpine 기반 JRE 이미지)
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# 컨테이너 리소스 제한에 맞춰 Java 메모리를 자동으로 최적화합니다.
# - MaxRAMPercentage: 컨테이너 메모리 제한의 75%를 JVM 힙으로 할당
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"

# 보안을 위해 비루트(Non-root) 사용자 설정
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# 빌드 단계에서 생성된 executable jar만 복사 (소유권 이전 포함)
COPY --chown=spring:spring --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# 환경 변수를 확장할 수 있도록 쉘 실행 형태를 사용하되, 신호 전달을 고려한 설정
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]