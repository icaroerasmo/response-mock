FROM maven:3.8.6-openjdk-18-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17
ENV portNumber 8082
COPY --from=build /home/app/target/response-mock-*.jar /usr/local/lib/response-mock.jar
EXPOSE $portNumber
ENTRYPOINT [ "java", "-Dspring.config.additional-location=/home/app/properties/application.yaml", "-Dserver.port=${portNumber}", "-jar", "/usr/local/lib/response-mock.jar" ]
