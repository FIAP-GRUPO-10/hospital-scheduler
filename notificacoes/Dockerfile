# Etapa de build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copia o pom.xml raiz
COPY pom.xml ./pom.xml

# Copia todos os módulos necessários
COPY agendamento ./agendamento
COPY notificacoes ./notificacoes
COPY historico ./historico

# Compila apenas o módulo notificacoes (gera JAR executável)
RUN mvn -q -DskipTests -pl notificacoes -am clean package

# Etapa de runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia apenas o JAR reempacotado (ignora .original)
COPY --from=build /workspace/notificacoes/target/notificacoes-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
