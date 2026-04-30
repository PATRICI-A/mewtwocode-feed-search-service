# Etapa 1 — build: compila el proyecto y genera el .jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Descarga dependencias primero (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -Djacoco.skip=true -q

# Etapa 2 — runtime: imagen liviana solo para ejecutar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
