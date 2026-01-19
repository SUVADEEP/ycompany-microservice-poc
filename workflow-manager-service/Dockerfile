FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY workflow-manager-service/pom.xml ./workflow-manager-service/
COPY common/pom.xml ./common/
COPY api-gateway/pom.xml ./api-gateway/
COPY claim-service/pom.xml ./claim-service/
RUN mvn dependency:go-offline -B -pl workflow-manager-service -am || mvn dependency:resolve -B -pl workflow-manager-service -am
COPY workflow-manager-service/src ./workflow-manager-service/src
COPY common/src ./common/src
RUN mvn clean package -DskipTests -pl workflow-manager-service -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/workflow-manager-service/target/workflow-manager-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

