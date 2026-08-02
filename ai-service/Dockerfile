# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent POM and module POMs
COPY pom.xml .
COPY dashboard-service/pom.xml dashboard-service/
COPY battle-service/pom.xml battle-service/
COPY ai-service/pom.xml ai-service/

# Pre-fetch dependencies
RUN mvn dependency:go-offline -B -pl ai-service -am

# Copy source and build
COPY ai-service/src ai-service/src
RUN mvn clean package -pl ai-service -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/ai-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
