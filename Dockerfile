FROM openjdk:8u212-jdk
COPY target/kafka-streams-demo-1.0.0-jar-with-dependencies.jar /usr/src/myapp/
WORKDIR /usr/src/myapp
ENTRYPOINT ["java", "-cp", "kafka-streams-demo-1.0.0-jar-with-dependencies.jar", "com.lenovo.aiops.streams.StreamTestProcessor"]