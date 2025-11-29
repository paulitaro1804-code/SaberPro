FROM openjdk:17
COPY "./target/saberpro-system-1.0.0.jar" "app.jar"
EXPOSE 8081
ENTRYPOINT  [ "java", "-jar", "app.jar" ]