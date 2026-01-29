# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src
# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["mvn", "spring-boot:run"]
