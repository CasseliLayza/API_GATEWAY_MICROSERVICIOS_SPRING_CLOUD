FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/SPRING_CLOUD_API_GATEWAY-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} msvc_gateway_server.jar
EXPOSE 8090
CMD ["java", "-jar", "msvc_gateway_server.jar"]