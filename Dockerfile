FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM amazoncorretto:21-alpine-full
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
