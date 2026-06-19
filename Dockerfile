# Stage 1:Build the project
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml for dependency caching
COPY pom.xml .

# Download dependency
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package

# Stage 2:Run the application
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar from build
COPY --from=build /app/target/*jar app.jar

# Expose Spring Boot port
EXPOSE 8080

#Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


