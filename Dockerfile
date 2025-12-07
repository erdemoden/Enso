# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/*.jar app.jar

# Java 21 ZGC + Generational
ENV JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+ZGenerational"

# HTTP + Game ports
EXPOSE 8080
EXPOSE 11908/tcp
EXPOSE 11907/udp

CMD ["java","-jar","app.jar"]