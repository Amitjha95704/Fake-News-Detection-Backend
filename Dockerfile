# Step 1: Maven se project ko build karna
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Java app ko run karna (Updated Image)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render ke liye port 8080 open karna
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]