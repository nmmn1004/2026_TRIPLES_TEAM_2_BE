# 1단계: 빌드 (JDK 사용)
# eclipse-temurin 대신 모든 아키텍처를 지원하는 bellsoft 이미지를 사용합니다.
FROM bellsoft/liberica-openjdk-alpine:17 AS build
WORKDIR /app

# 빌드 속도 최적화: 종속성 캐싱
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 (JRE 사용)
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# 보안을 위해 비루트 사용자 추가
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]