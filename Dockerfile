# Use lightweight Java 17 runtime
FROM eclipse-temurin:17-jre

# Set working directory inside container
WORKDIR /app

# Copy Spring Boot JAR into container
COPY target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
