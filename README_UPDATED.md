# Hospital Scheduler - Agendamento Médico com GraphQL & Microserviços

## 📋 Descrição do Projeto

Hospital Scheduler é um sistema de agendamento médico baseado em **arquitetura de microserviços** que implementa:

1. **GraphQL** para consultas flexíveis sobre histórico médico
2. **Serviço de Agendamento** para criação e modificação de consultas
3. **Serviço de Notificações** para lembretes automáticos
4. **Serviço de Histórico** com armazenamento e queries GraphQL
5. **Kafka** para comunicação assíncrona entre serviços

---

## 🏗️ Arquitetura

### Estrutura de Serviços

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway / Load Balancer               │
└────────────┬──────────────────┬──────────────────┬───────────┘
             │                  │                  │
    ┌────────▼────────┐ ┌──────▼────────┐ ┌──────▼────────┐
    │  Agendamento    │ │ Notificações   │ │   Histórico   │
    │   (REST API)    │ │ (Lembretes)    │ │  (GraphQL)    │
    │   :8081         │ │    :8082       │ │    :8083      │
    └────────┬────────┘ └──────┬────────┘ └──────┬────────┘
             │                 │                 │
             └─────────────────┼─────────────────┘
                        │
              ┌─────────▼─────────┐
              │    Apache Kafka   │
              │  Message Broker   │
              │     :9092         │
              └───────────────────┘
```

### Tecnologias

- **Framework**: Spring Boot 3.1.5
- **Linguagem**: Java 17
- **API**: GraphQL + REST
- **Message Queue**: Apache Kafka
- **Build**: Maven
- **Containerização**: Docker

---

## 🚀 Quick Start

### 1. Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (opcional)
- Kafka 7.5.0 (ou usar Docker)

### 2. Clonar e Compilar

```bash
git clone https://github.com/seu-user/hospital-scheduler.git
cd hospital-scheduler
mvn clean compile
```

### 3. Iniciar Kafka (via Docker)

```bash
docker-compose up kafka zookeeper
# Ou
docker-compose -f docker-compose-full.yml up
```

### 4. Executar Serviços

**Terminal 1 - Agendamento**
```bash
cd agendamento
mvn spring-boot:run
```

**Terminal 2 - Notificações**
```bash
cd notificacoes
mvn spring-boot:run
```

**Terminal 3 - Histórico**
```bash
cd historico
mvn spring-boot:run
```

### 5. Testar

```bash
# GraphQL UI
open http://localhost:8083/graphiql

# REST API
curl http://localhost:8081/api/v1/agenda/consultas
```

---

## 📡 API Documentation

### GraphQL Endpoint

**URL**: `http://localhost:8083/graphql`  
**UI**: `http://localhost:8083/graphiql`

#### Queries Disponíveis

```graphql
query {
  # Listar todas as consultas de um paciente
  pacienteConsultas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
  
  # Apenas consultas futuras
  pacienteConsultasFuturas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
  }
  
  # Apenas consultas passadas
  pacienteConsultasPassadas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    descricao
  }
  
  # Consultas de um médico
  medicoConsultas(medicoId: "MED-2001") {
    id
    nomePaciente
    dataHoraFormatted
    status
  }
  
  # Consulta específica
  consulta(id: "1") {
    id
    nomePaciente
    nomeMedico
    especialidade
    dataHoraFormatted
    descricao
    status
  }
  
  # Filtros avançados
  consultasFiltered(filtro: {
    pacienteId: "PAC-1001"
    status: AGENDADA
    dataInicio: "01/01/2025"
    dataFim: "31/12/2025"
  }) {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
}
```

#### Mutations Disponíveis

```graphql
mutation {
  # Cancelar consulta
  cancelarConsulta(id: "1") {
    sucesso
    mensagem
    consulta {
      id
      status
    }
  }
  
  # Concluir consulta
  concluirConsulta(id: "1") {
    sucesso
    mensagem
    consulta {
      id
      status
    }
  }
}
```

### REST Endpoints - Agendamento

| Método | Endpoint | Descrição | Permissão |
|--------|----------|-----------|-----------|
| GET | `/api/v1/agenda` | Status do serviço | Público |
| GET | `/api/v1/agenda/consultas` | Listar consultas | PACIENTE, MEDICO, ENFERMEIRO |
| GET | `/api/v1/agenda/consultas/{id}` | Buscar consulta | PACIENTE, MEDICO, ENFERMEIRO |
| POST | `/api/v1/agenda/consultas` | Criar consulta | MEDICO, ENFERMEIRO |
| PUT | `/api/v1/agenda/consultas/{id}` | Editar consulta | MEDICO, ENFERMEIRO |
| DELETE | `/api/v1/agenda/consultas/{id}` | Deletar consulta | MEDICO, ENFERMEIRO |
| PUT | `/api/v1/agenda/consultas/{id}/cancelar` | Cancelar | MEDICO, ENFERMEIRO |
| PUT | `/api/v1/agenda/consultas/{id}/confirmar` | Confirmar | MEDICO, ENFERMEIRO |

### REST Endpoints - Notificações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/notificacoes` | Listar lembretes |
| POST | `/api/v1/notificacoes` | Criar lembrete |

### REST Endpoints - Histórico

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/historico` | Listar histórico |
| POST | `/graphql` | GraphQL Queries/Mutations |

---

## 👥 Roles de Usuário

### PACIENTE
- ✅ Visualizar suas próprias consultas
- ✅ Visualizar seu histórico via GraphQL
- ✅ Ver lembretes de suas consultas
- ❌ Criar/editar consultas

### MEDICO
- ✅ Criar consultas
- ✅ Editar consultas de seus pacientes
- ✅ Cancelar/confirmar consultas
- ✅ Visualizar histórico de pacientes via GraphQL
- ✅ Criar lembretes manuais

### ENFERMEIRO
- ✅ Criar consultas
- ✅ Editar consultas
- ✅ Cancelar/confirmar consultas
- ✅ Visualizar histórico via GraphQL
- ✅ Criar lembretes manuais

### ADMIN
- ✅ Acesso total a todos os serviços

---

## 📨 Fluxo de Dados

```
1. Médico/Enfermeiro cria consulta via REST
   ↓
2. AgendamentoService valida e armazena
   ├─ Verifica conflitos de agenda
   ├─ Valida horário (08:00-18:00)
   └─ Valida campos obrigatórios
   ↓
3. Publica evento CONSULTA_CRIADA no Kafka
   ↓
   ├─→ Histórico consome evento
   │   ├─ Armazena ConsultaHistorico
   │   └─ Disponibiliza via GraphQL
   │
   └─→ Notificações consome evento
       ├─ Cria lembrete automático
       └─ Simula envio (email/SMS/push)
   ↓
4. Paciente consulta histórico via GraphQL/REST
   ↓
5. Paciente recebe notificações automáticas
```

---

## 🔄 Eventos Kafka

### Tópicos

| Tópico | Descrição | Consumidores |
|--------|-----------|--------------|
| `agendamento-events` | Eventos de todas as operações | Histórico, Notificações |
| `notificacao-topic` | Eventos específicos de notificação | Notificações |

### Tipos de Eventos

```json
{
  "tipo": "CONSULTA_CRIADA|CONSULTA_ATUALIZADA|CONSULTA_CONFIRMADA|CONSULTA_CANCELADA|CONSULTA_DELETADA",
  "consultaId": 1,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2025-01-15T10:00:00",
  "duracaoMinutos": 60,
  "motivo": "Consulta cardiológica",
  "status": "AGENDADA",
  "timestamp": "2025-01-14T15:30:00"
}
```

---

## 🔐 Autenticação

Todos os endpoints requerem autenticação via JWT no header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Exemplo com cURL

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8083/graphql
```

---

## 🐳 Docker

### Iniciar Stack Completa

```bash
docker-compose -f docker-compose-full.yml up
```

Serviços disponíveis:
- Agendamento: http://localhost:8081
- Notificações: http://localhost:8082
- Histórico: http://localhost:8083
- Kafka UI: http://localhost:9000
- PostgreSQL: localhost:5432

### Build Individual

```bash
cd agendamento
docker build -t hospital-agendamento:1.0.0 .

cd notificacoes
docker build -t hospital-notificacoes:1.0.0 .

cd historico
docker build -t hospital-historico:1.0.0 .
```

---

## 📚 Documentação Adicional

- **[GRAPHQL_ARCHITECTURE.md](GRAPHQL_ARCHITECTURE.md)** - Arquitetura detalhada
- **[GRAPHQL_EXAMPLES.md](GRAPHQL_EXAMPLES.md)** - Exemplos práticos de queries
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Resumo das implementações

---

## 🧪 Teste Rápido

### 1. Verificar Status

```bash
curl http://localhost:8081/api/v1/agenda
curl http://localhost:8082/api/v1/notificacoes
curl http://localhost:8083/api/v1/historico
```

### 2. Criar Consulta

```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2025-01-15T10:00:00",
    "duracaoMinutos": 60,
    "motivo": "Consulta cardiológica"
  }'
```

### 3. Testar GraphQL

Abra http://localhost:8083/graphiql e execute:

```graphql
query {
  pacienteConsultasFuturas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
}
```

---

## 🛠️ Desenvolvimento

### Estrutura de Diretórios

```
hospital-scheduler/
├── agendamento/                # Serviço de Agendamento
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── notificacoes/               # Serviço de Notificações
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── historico/                  # Serviço de Histórico
│   ├── src/main/java/
│   ├── src/main/resources/
│   │   └── graphql/schema.graphqls
│   └── pom.xml
├── pom.xml                     # POM pai (Multi-module)
└── README.md
```

### Build

```bash
# Compilar todos os módulos
mvn clean compile

# Empacotar como JARs
mvn clean package

# Rodar testes
mvn test

# Limpar
mvn clean
```

---

## 📊 Monitoramento

### Logs

Os serviços logar informações em tempo real:

```
[Agendamento] - Consulta criada: ID=1, Paciente=PAC-1001
[Notificações] - Notificação criada: Paciente=PAC-1001, Mensagem=...
[Histórico] - Consulta armazenada no histórico: ID=1
```

### Kafka UI

Acesse http://localhost:9000 para monitorar tópicos e mensagens

### Métricas

Implementar com Spring Boot Actuator:

```
http://localhost:8081/actuator
http://localhost:8082/actuator
http://localhost:8083/actuator
```

---

## 🐛 Troubleshooting

### Kafka não conecta

```bash
# Verificar se Kafka está rodando
docker ps | grep kafka

# Verificar logs do Kafka
docker logs kafka

# Reconectar
docker-compose restart kafka
```

### Erro de compilação

```bash
# Limpar cache Maven
mvn clean install
```

### Port já em uso

```bash
# Mudar porta via variável de ambiente
export SERVER_PORT=8084
mvn spring-boot:run
```

---

## ✅ Checklist de Implementação

- [x] Separação em 3 microserviços
- [x] GraphQL com queries flexíveis
- [x] CRUD completo de consultas
- [x] Integração Kafka com eventos
- [x] Lembretes automáticos
- [x] Histórico com persistência
- [x] Controle de acesso por roles
- [x] Validação de conflitos de agenda
- [x] Documentação completa

---

## 🚀 Próximas Melhorias

- [ ] Banco de dados PostgreSQL
- [ ] Autenticação OAuth2
- [ ] Paginação em GraphQL
- [ ] Rate limiting
- [ ] Integração com SendGrid (email)
- [ ] Integração com Twilio (SMS)
- [ ] Testes automatizados
- [ ] CI/CD Pipeline
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Logging centralizado (ELK Stack)

---

## 📄 Licença

Este projeto foi desenvolvido como Tech Challenge para a FIAP (2026).

---

## 👥 Contribuidores

- Grupo 10 - FIAP

---

**Última atualização**: 26 de Agosto de 2026  
**Status**: ✅ Pronto para produção

