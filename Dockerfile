FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build target/grove-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx350m", "-Xms200m", "-jar", "app.jar"]
