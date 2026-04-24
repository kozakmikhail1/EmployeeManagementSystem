FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline
COPY src src
COPY frontend frontend
COPY checkstyle.xml settings.json ./
RUN ./mvnw -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=builder /app/frontend /app/frontend
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
