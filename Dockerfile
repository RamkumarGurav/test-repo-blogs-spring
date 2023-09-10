# Use a Java base image
FROM openjdk:17-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the Spring Boot application JAR file into the Docker image
COPY target/test-myblogs-spring-mongo.jar /app/test-myblogs-spring-mongo.jar


# Set environment variables
ENV SECRET_1=first_secret
ENV SECRET_2=second_secret

# Expose the port that the Spring Boot application is listening on
#useful for during development
EXPOSE 7000

# Run the Spring Boot application when the container starts
CMD ["java", "-jar", "test-myblogs-spring-mongo.jar"]