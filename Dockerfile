# Build stage
FROM maven:3.9-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code
COPY backend/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/volunteer-portal-1.0.0.jar ./app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
