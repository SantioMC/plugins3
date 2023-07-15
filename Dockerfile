FROM openjdk:17-jdk-slim
LABEL author="Santio"
COPY build/libs/plugins3.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]