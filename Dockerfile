FROM eclipse-temurin:21-jdk-jammy as builder

LABEL maintainer="Ilia Ivanov"
LABEL version="1.0"
LABEL description="Bank application"

WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="Ilia Ivanov"
LABEL version="1.0"

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

ENV JAVA_OPTS="-Xmx512m -Xms256m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]