FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml .

# Copy all module POMs (needed for multi-module build)
COPY api-gateway/pom.xml ./api-gateway/
COPY common/pom.xml ./common/
COPY claim-service/pom.xml ./claim-service/
COPY workflow-manager-service/pom.xml ./workflow-manager-service/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B -pl api-gateway -am || mvn dependency:resolve -B -pl api-gateway -am

# Copy source code
COPY api-gateway/src ./api-gateway/src
COPY common/src ./common/src

# Build only api-gateway and its dependencies
RUN mvn clean package -DskipTests -pl api-gateway -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR (Spring Boot creates a -SNAPSHOT.jar or specific version)
COPY --from=build /app/api-gateway/target/api-gateway-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
