FROM openjdk:17-jdk-slim

WORKDIR /app

COPY EduFlow/springboot/target/*.jar app.jar

EXPOSE 9090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=9090"]
