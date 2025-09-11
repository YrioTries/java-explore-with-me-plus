FROM openjdk:21-jdk-slim

WORKDIR /app

COPY statSvc/server/target/server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]