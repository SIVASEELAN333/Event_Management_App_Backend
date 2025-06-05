# Use official Java runtime base image (JDK 17 for example)
FROM eclipse-temurin:17-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy your built jar file into container
COPY target/eventapp-0.0.1-SNAPSHOT.jar app.jar

# Expose port that your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Command to run your jar file
ENTRYPOINT ["java","-jar","app.jar"]
