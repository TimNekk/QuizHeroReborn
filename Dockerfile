FROM openjdk:19-jdk
WORKDIR /app
COPY target/quiz-hero-reborn*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
