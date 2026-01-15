#FROM openjdk:17-jdk
#
#ARG JAR_FILE=build/libs/*SNAPSHOT.jar
#
#COPY ${JAR_FILE} app.jar
#
#ENTRYPOINT ["java","-jar","/app.jar"]
#
## 1단계: 빌드
#FROM eclipse-temurin:17-jdk-alpine AS build
#WORKDIR /app
#COPY . .
#RUN chmod +x ./gradlew
#RUN ./gradlew clean build -x test
#
## 2단계: 실행
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]


# 1단계: 빌드
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
