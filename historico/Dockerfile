FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY agendamento ./agendamento
COPY notificacoes ./notificacoes
COPY historico ./historico

RUN mvn -q -DskipTests -pl historico -am clean package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/historico/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
