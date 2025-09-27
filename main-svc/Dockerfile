FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/main-svc-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} main-svc.jar
ENTRYPOINT ["java", "-jar", "/main-svc.jar"]
