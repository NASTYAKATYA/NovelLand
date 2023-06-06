FROM maven:3.8.3-openjdk-11-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/NovelLand-0.0.1-SNAPSHOT.jar .

EXPOSE 8080
CMD ["java", "-jar", "NovelLand-0.0.1-SNAPSHOT.jar"]




