FROM maven:3.8.5-openjdk-17 as build

COPY . .

RUN maven clean package -DskipTests

FROM openjdk:17.0.01-jdk-slim

COPY --from=build /target/spring-mongo-docker-compose.jar spring-mongo-docker-compose.jar

EXPOSE 7000

ENTRYPOINT ["java","-jar","spring-mongo-docker-compose.jar"]