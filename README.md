# Response Mock

Simple but useful mock application. Just send a request with all values you want as response.

#### Getting started
###### Local
```
mvn clean package
java -Dserver.port=8081 -jar target/response-mock-<version>.jar
```
###### Docker

If you want to run it in docker, simply do:
```
mvn clean package
```
and then:
```
FROM amazoncorretto:17
ENV portNumber 8080
WORKDIR app
COPY target/response-mock-*.jar ./response-mock.jar
EXPOSE $portNumber
ENTRYPOINT [ "java", "-Dserver.port=${portNumber}", "-jar", "response-mock.jar" ]
```

ps.: Just run response-mock.dockerfile present in root directory if this setting is enough for your purpose.

#### Parameters examples

- body
  - &lt;body&gt;hello world&lt;/body&gt;
  - {"description": "validation failed"}
  - ...
- httpStatus
  - 200
  - 400
  - 500
  - ...
- produces
  - application/json
  - application/xml
  - ...
- headers
  - header1=value1;header2=value2;header3=value3
  - location: http://www.google.com

#### Execution

Just use a http client such as Postman, Imsonmnia, your browser or something else to send the request as below:

&lt;hostname&gt;?body=%7B%22description%22%3A%20%22validation%20failed%22%7D&httpStatus=400&headers=header1%3Dvalue1%3Bheader2%3Dvalue2%3Bheader3%3Dvalue3&produces=application%2Fjson
