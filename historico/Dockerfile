# Etapa de build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copia o pom.xml raiz
COPY pom.xml ./pom.xml

# Copia todos os módulos necessários
COPY agendamento ./agendamento
COPY notificacoes ./notificacoes
COPY historico ./historico

# Compila apenas o módulo historico
RUN mvn -q -DskipTests -pl historico -am clean package

# Etapa de runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia apenas o JAR reempacotado (ignora .original)
COPY --from=build /workspace/historico/target/historico-1.0.0.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
