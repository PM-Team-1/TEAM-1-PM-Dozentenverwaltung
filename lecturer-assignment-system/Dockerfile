FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar
COPY database.db database.db

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]