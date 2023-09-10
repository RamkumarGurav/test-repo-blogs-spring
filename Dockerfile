FROM openjdk:17.0.1-jdk-slim

ADD target/test-myblogs-spring-mongo.jar test-myblogs-spring-mongo.jar

EXPOSE 7000

ENTRYPOINT ["java","-jar","test-myblogs-spring-mongo.jar"]