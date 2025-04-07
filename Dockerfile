FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/hstime-0.0.1.jar
COPY ${JAR_FILE} app_hstime.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_hstime.jar"]