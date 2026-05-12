# Stage 1 — build: compile and package the JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately so this layer is cached when only src/ changes
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -Djacoco.skip=true -q

# Stage 2 — runtime: minimal image with only the JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the port defined in application.properties (server.port=8080)
EXPOSE 8080

# Container-aware JVM flags:
#   UseContainerSupport  — honour cgroup CPU/memory limits
#   MaxRAMPercentage     — use at most 75% of the container's RAM for the heap
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
