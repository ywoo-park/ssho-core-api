FROM openjdk:8-jre
ARG JAR_FILE=target/core-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} core-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/core-0.0.1-SNAPSHOT.jar"]