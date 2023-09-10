FROM maven:3.8.5-openjdk-17 as build

ADD target/spring-mongo-docker-compose.jar /spring-mongo-docker-compose.jar

EXPOSE 7000

ENTRYPOINT ["java","-jar","spring-mongo-docker-compose.jar"]