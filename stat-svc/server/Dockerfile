FROM eclipse-temurin:21-jdk-alpine

COPY target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app.jar"]