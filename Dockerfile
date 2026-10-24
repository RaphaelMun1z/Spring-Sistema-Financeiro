FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build --chown=10001:10001 /app/target/gestao-financeira-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
ENV TZ=America/Sao_Paulo
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
