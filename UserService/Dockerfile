FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY build/libs/UserService-0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]