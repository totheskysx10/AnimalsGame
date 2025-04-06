FROM maven:3.9.8 AS build-java
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build-java /app/target/*.jar app.jar
EXPOSE 5001
CMD ["java", "-jar", "app.jar"]
