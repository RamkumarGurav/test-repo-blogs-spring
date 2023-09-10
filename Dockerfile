# Use the official Maven image with OpenJDK 17 as the build stage.
FROM maven:3.8.5-openjdk-17 as build

# Copy the entire context (your project files) into the Docker image.
COPY . .

# Run the Maven command to clean the project and package it.
# The "-DskipTests" flag skips running tests during the build.
RUN mvn clean package -DskipTests

# Start a new stage for the final image.
# Use the official OpenJDK 17 image with a slim base (smaller image size).
FROM openjdk:17.0.1-jdk-slim

# Copy the JAR file generated in the "build" stage into this stage.
COPY --from=build /target/test-myblogs-spring-mongo.jar test-myblogs-spring-mongo.jar

# Expose port 7000 (the port your Spring Boot application listens on).
EXPOSE 8000

# Define the entry point for the Docker container.
# This command will be executed when the container is started.
ENTRYPOINT ["java", "-jar", "test-myblogs-spring-mongo.jar"]