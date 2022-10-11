FROM openjdk:11.0.7-jre-slim
COPY /target/realt-parser-0.0.1-SNAPSHOT.jar /demo.jar
CMD ["java", "-jar", "/demo.jar"]
EXPOSE 8080