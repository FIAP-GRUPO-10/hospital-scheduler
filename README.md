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

Regras implementadas com base no ADJT

- Médicos e enfermeiros podem registrar e editar consultas no módulo de agendamento.
- Pacientes só podem consultar seu próprio histórico e seus lembretes.
- A regra de conflito de agenda impede sobreposição de horários para o mesmo médico, enfermeiro ou paciente.
- A consulta deve ocorrer em horário comercial e em data futura.
- O serviço de notificações representa o fluxo assíncrono de lembretes após o agendamento ou edição de consultas.

Autenticação e autorização JWT

- Faça login em cada serviço com POST /api/v1/auth/login usando as credenciais do perfil:
  - Médico: MED-2001 / medico123
  - Enfermeiro: ENF-3001 / enfermeiro123
  - Paciente: PAC-1001 / paciente123
- Envie o token em Authorization: Bearer <token> nas requisições protegidas.

Endpoints principais do módulo de agendamento

- POST /api/v1/auth/login
- GET /api/v1/agenda/consultas
- POST /api/v1/agenda/consultas
- PUT /api/v1/agenda/consultas/{id}

Endpoints principais do módulo de histórico

- POST /api/v1/auth/login
- GET /api/v1/historico/consultas
- GET /api/v1/historico/pacientes/{pacienteId}/consultas

Endpoints principais do módulo de notificações

- POST /api/v1/auth/login
- GET /api/v1/notificacoes/lembretes
- POST /api/v1/notificacoes/lembretes