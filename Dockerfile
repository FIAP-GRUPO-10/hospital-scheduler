# Multi-stage Dockerfile for building and running the Spring Boot app

FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy pom first for better caching
COPY pom.xml ./
COPY src ./src

# build the application (skip tests for faster image build)
RUN mvn -B -DskipTests package --no-transfer-progress

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# install curl for healthcheck
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Copy the executable jar from the build stage
COPY --from=build /workspace/target/hospital-scheduler-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
