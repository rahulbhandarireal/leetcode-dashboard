# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent POM and module POMs
COPY pom.xml .
COPY dashboard-service/pom.xml dashboard-service/
COPY battle-service/pom.xml battle-service/
COPY ai-service/pom.xml ai-service/

# Pre-fetch dependencies
RUN mvn dependency:go-offline -B -pl battle-service -am

# Copy source and build
COPY battle-service/src battle-service/src
RUN mvn clean package -pl battle-service -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install Docker CLI so the container can execute docker commands
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/battle-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
