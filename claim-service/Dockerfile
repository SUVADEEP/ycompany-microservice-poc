FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY claim-service/pom.xml ./claim-service/
COPY common/pom.xml ./common/
COPY api-gateway/pom.xml ./api-gateway/
COPY workflow-manager-service/pom.xml ./workflow-manager-service/
RUN mvn dependency:go-offline -B -pl claim-service -am || mvn dependency:resolve -B -pl claim-service -am
COPY claim-service/src ./claim-service/src
COPY common/src ./common/src
RUN mvn clean package -DskipTests -pl claim-service -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/claim-service/target/claim-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

