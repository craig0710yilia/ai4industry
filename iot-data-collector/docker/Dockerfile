FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG JAR_FILE=../backend/collector-start/target/collector-start-*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir -p /app/backups /app/certs
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
