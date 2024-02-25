FROM amazoncorretto:17
ENV portNumber 8082
WORKDIR app
COPY target/response-mock-*.jar ./response-mock.jar
EXPOSE $portNumber
ENTRYPOINT [ "java", "-Dserver.port=${portNumber}", "-jar", "response-mock.jar" ]
