FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY agendamento/pom.xml ./agendamento/pom.xml
COPY agendamento/src ./agendamento/src
COPY notificacoes/pom.xml ./notificacoes/pom.xml
COPY notificacoes/src ./notificacoes/src
COPY historico/pom.xml ./historico/pom.xml
COPY historico/src ./historico/src

RUN mvn -q -DskipTests -pl historico -am clean package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/historico/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
