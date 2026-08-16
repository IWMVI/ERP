FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S erp && adduser -S erp -G erp \
    && mkdir -p /app/uploads \
    && chown -R erp:erp /app

COPY --from=build --chown=erp:erp /workspace/target/erp-*.jar /app/erp.jar

USER erp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/erp.jar"]

