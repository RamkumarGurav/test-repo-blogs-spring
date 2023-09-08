FROM openjdk:17

ADD target/spring-mongo-docker-compose.jar /spring-mongo-docker-compose.jar

ENTRYPOINT ["java","-jar","spring-mongo-docker-compose.jar"]