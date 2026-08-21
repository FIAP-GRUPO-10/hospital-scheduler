# 🏥 Hospital Scheduler

Projeto Java/Spring Boot organizado em módulos Maven para separar as responsabilidades do sistema hospitalar.

Estrutura do projeto

hospital-scheduler/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── agendamento/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── notificacoes/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── historico/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
└── README.md

Módulos

- agendamento: porta 8081
- notificacoes: porta 8082
- historico: porta 8083

Execução local

Na raiz do projeto:

- Build completo:
  mvn clean package

- Testes:
  mvn test

- Executar módulo individualmente:
  cd agendamento && mvn spring-boot:run
  cd notificacoes && mvn spring-boot:run
  cd historico && mvn spring-boot:run

Endpoints principais

- Agendamento: http://localhost:8081/api/v1/agenda
- Notificações: http://localhost:8082/health
- Histórico: http://localhost:8083/api/v1/historico

Execução com Docker Compose

Na raiz do projeto:

- Subir todos os serviços:
  docker compose up --build

- Parar os serviços:
  docker compose down

Observações

- O Maven usa `pom.xml` na raiz como projeto agregador dos módulos.
- Cada módulo possui seu próprio `pom.xml`, `Dockerfile` e configuração local em `src/main/resources/application.properties`.
- Os serviços são isolados por porta para evitar conflitos durante o desenvolvimento local e em containers.