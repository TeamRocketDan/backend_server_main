FROM --platform=linux/amd64 openjdk:11
COPY build/libs/Rocket-0.0.1-SNAPSHOT.jar Rocket-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "Rocket-0.0.1-SNAPSHOT.jar"]