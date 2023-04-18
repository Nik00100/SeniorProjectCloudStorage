FROM openjdk:11

VOLUME /tmp

EXPOSE 8888

COPY target/SeniorProjectCloudStorage-0.0.1-SNAPSHOT.jar application.jar

ADD src/main/resources/application.properties src/main/resources/application.properties

ENTRYPOINT ["java", "-jar", "/application.jar"]