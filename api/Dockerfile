# Imagen para desplegar en Render (u otro hosting con Docker).
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/heartbeat-api-all.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
