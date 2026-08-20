# hospital-scheduler

Sistema hospitalar em Java/Spring Boot com GraphQL e RabbitMQ para agendamento de consultas e notificações.

Docker

- Build da imagem:

  docker build -t hospital-scheduler .

- Rodar localmente (imagem):

  docker run -p 8080:8080 hospital-scheduler

- Usando docker-compose:

  docker-compose up --build

Healthcheck

- Endpoint: GET /health -> {"status":"UP"}
- O docker-compose inclui um healthcheck que usa /health para verificar se o serviço está UP.

Testes rápidos

- Build da aplicação: mvn clean package
- Rodar durante desenvolvimento: mvn spring-boot:run
- Verificar health: curl http://localhost:8080/health