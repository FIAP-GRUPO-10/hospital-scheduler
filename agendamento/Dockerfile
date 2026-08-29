FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copia o pom.xml raiz
COPY pom.xml ./pom.xml

# Copia todos os módulos
COPY agendamento ./agendamento
COPY notificacoes ./notificacoes
COPY historico ./historico

# Compila apenas o módulo agendamento
RUN mvn -q -DskipTests -pl agendamento -am clean package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/agendamento/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
