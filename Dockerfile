# Build stage
#
FROM maven:3.6.3-jdk-8-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install

#
# Package stage
#
FROM openjdk:8-jre-slim
COPY --from=build /home/app/target/data-service-1.0.jar /usr/local/lib/data-service-1.0.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/data-service-1.0.jar"]
