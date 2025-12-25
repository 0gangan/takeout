# Dockerfile
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY target/takeout-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
