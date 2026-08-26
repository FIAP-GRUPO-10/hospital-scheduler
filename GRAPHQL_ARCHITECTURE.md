# Hospital Scheduler - GraphQL & Microservices Architecture

## Visão Geral da Arquitetura

O projeto Hospital Scheduler é baseado em **arquitetura de microserviços** com separação clara de responsabilidades:

### Serviços

#### 1. **Serviço de Agendamento** (Porta 8081)
- **Responsabilidade**: Criar, editar e gerenciar consultas
- **Endpoints REST**:
  - `GET /api/v1/agenda/consultas` - Listar todas as consultas
  - `GET /api/v1/agenda/consultas/{id}` - Buscar consulta específica
  - `POST /api/v1/agenda/consultas` - Criar nova consulta
  - `PUT /api/v1/agenda/consultas/{id}` - Atualizar consulta
  - `DELETE /api/v1/agenda/consultas/{id}` - Deletar consulta
  - `PUT /api/v1/agenda/consultas/{id}/cancelar` - Cancelar consulta
  - `PUT /api/v1/agenda/consultas/{id}/confirmar` - Confirmar consulta

- **Permissões**:
  - ✅ MEDICO - Pode criar, editar e cancelar consultas
  - ✅ ENFERMEIRO - Pode criar, editar e cancelar consultas
  - ✅ PACIENTE - Pode visualizar consultas

- **Integração Kafka**: Publica eventos de consulta para notificações e histórico

#### 2. **Serviço de Histórico** (Porta 8083)
- **Responsabilidade**: Armazenar e disponibilizar consultas via GraphQL
- **Tecnologia**: GraphQL + REST
- **Endpoints**:
  - `GET /api/v1/historico` - REST: Listar histórico
  - `POST /graphql` - GraphQL: Queries e Mutations

- **GraphQL Queries Disponíveis**:
  ```graphql
  query {
    pacienteConsultas(pacienteId: "PAC-1001") {
      id
      nomePaciente
      nomeMedico
      dataHoraFormatted
      status
    }
  }
  
  query {
    pacienteConsultasFuturas(pacienteId: "PAC-1001") {
      id
      nomePaciente
      nomeMedico
      dataHoraFormatted
      status
    }
  }
  
  query {
    pacienteConsultasPassadas(pacienteId: "PAC-1001") {
      id
      nomePaciente
      nomeMedico
      dataHoraFormatted
      status
    }
  }
  
  query {
    medicoConsultas(medicoId: "MED-2001") {
      id
      nomePaciente
      dataHoraFormatted
      status
    }
  }
  
  query {
    consulta(id: "1") {
      id
      nomePaciente
      nomeMedico
      dataHoraFormatted
      descricao
      status
    }
  }
  
  query {
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

- **GraphQL Mutations Disponíveis**:
  ```graphql
  mutation {
    cancelarConsulta(id: "1") {
      sucesso
      mensagem
      consulta {
        id
        status
      }
    }
  }
  
  mutation {
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

- **Interface GraphiQL**: Acesse http://localhost:8083/graphiql para testar queries

- **Permissões**:
  - ✅ PACIENTE - Pode visualizar seu próprio histórico
  - ✅ MEDICO - Pode visualizar histórico de seus pacientes
  - ✅ ENFERMEIRO - Pode visualizar histórico de seus pacientes
  - ✅ ADMIN - Acesso total

#### 3. **Serviço de Notificações** (Porta 8082)
- **Responsabilidade**: Enviar lembretes automáticos aos pacientes
- **Endpoints REST**:
  - `GET /api/v1/notificacoes` - Listar lembretes
  - `POST /api/v1/notificacoes` - Criar lembrete manual

- **Integração Kafka**: Consome eventos de agendamento e cria notificações automaticamente

- **Eventos Processados**:
  - `CONSULTA_CRIADA` - Cria lembrete quando consulta é agendada
  - `CONSULTA_ATUALIZADA` - Notifica quando consulta é modificada
  - `CONSULTA_CONFIRMADA` - Envia confirmação com instruções
  - `CONSULTA_CANCELADA` - Notifica cancelamento
  - `CONSULTA_DELETADA` - Notifica deleção

---

## Fluxo de Dados entre Serviços

```
┌──────────────────┐
│   Agendamento    │
│   (CRUD)         │
└────────┬─────────┘
         │
         │ Publica evento no Kafka
         │ (CONSULTA_CRIADA, etc)
         │
         ├─────────────┬─────────────┐
         ▼             ▼             ▼
    ┌────────┐  ┌────────────┐  ┌──────────┐
    │Histórico│  │Notificações│  │  (Future)│
    │(GraphQL)│  │ (Lembretes)│  │ Services │
    └────────┘  └────────────┘  └──────────┘
         │             │
         │ Armazena    │ Envia Notificações
         │ Histórico   │ ao Paciente
         │             │
         └─────┬───────┘
               │
               ▼
         Paciente recebe
         lembrete de
         consulta futura
```

---

## Como Usar GraphQL

### 1. Acessar GraphiQL (Interface Visual)

```bash
# Em seu navegador, acesse:
http://localhost:8083/graphiql
```

### 2. Realizar Queries GraphQL via cURL

```bash
# Listar todas as consultas de um paciente
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "query": "query { pacienteConsultas(pacienteId: \"PAC-1001\") { id nomePaciente dataHoraFormatted status } }"
  }'

# Listar apenas consultas futuras
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "query": "query { pacienteConsultasFuturas(pacienteId: \"PAC-1001\") { id nomePaciente dataHoraFormatted status } }"
  }'

# Cancelar uma consulta
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "query": "mutation { cancelarConsulta(id: \"1\") { sucesso mensagem consulta { id status } } }"
  }'
```

### 3. Realizar Queries via REST

```bash
# Listar histórico de um paciente
curl http://localhost:8083/api/v1/historico?pacienteId=PAC-1001 \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

---

## Schema GraphQL Completo

```graphql
type Query {
  # Retorna todas as consultas de um paciente
  pacienteConsultas(pacienteId: ID!): [Consulta!]!
  
  # Retorna apenas consultas futuras de um paciente
  pacienteConsultasFuturas(pacienteId: ID!): [Consulta!]!
  
  # Retorna apenas consultas passadas de um paciente
  pacienteConsultasPassadas(pacienteId: ID!): [Consulta!]!
  
  # Busca consultas por médico
  medicoConsultas(medicoId: ID!): [Consulta!]!
  
  # Busca consulta específica pelo ID
  consulta(id: ID!): Consulta
  
  # Busca consultas com filtros avançados
  consultasFiltered(filtro: ConsultaFiltro!): [Consulta!]!
}

type Consulta {
  id: ID!
  pacienteId: ID!
  nomePaciente: String!
  emailPaciente: String!
  medicoId: ID!
  nomeMedico: String!
  especialidade: String!
  dataHora: String!
  dataHoraFormatted: String!
  descricao: String
  status: StatusConsulta!
  tipoConsulta: String
  criadaEm: String!
  atualizadaEm: String
}

enum StatusConsulta {
  AGENDADA
  CONFIRMADA
  CONCLUIDA
  CANCELADA
  REMARCADA
}

input ConsultaFiltro {
  pacienteId: ID
  medicoId: ID
  status: StatusConsulta
  dataInicio: String
  dataFim: String
  especialidade: String
}

type Mutation {
  # Cancela uma consulta
  cancelarConsulta(id: ID!): ConsultaResult!
  
  # Marca consulta como concluída
  concluirConsulta(id: ID!): ConsultaResult!
}

type ConsultaResult {
  sucesso: Boolean!
  mensagem: String!
  consulta: Consulta
}
```

---

## Roles de Usuário e Permissões

### PACIENTE
- ✅ Visualizar suas próprias consultas
- ✅ Visualizar seu histórico via GraphQL
- ✅ Visualizar lembretes
- ❌ Criar consultas
- ❌ Editar consultas

### MEDICO
- ✅ Criar consultas
- ✅ Editar consultas
- ✅ Cancelar consultas
- ✅ Confirmar consultas
- ✅ Visualizar histórico de pacientes via GraphQL
- ✅ Visualizar consultas de seus pacientes
- ✅ Criar lembretes manuais

### ENFERMEIRO
- ✅ Criar consultas
- ✅ Editar consultas
- ✅ Cancelar consultas
- ✅ Confirmar consultas
- ✅ Visualizar histórico de pacientes via GraphQL
- ✅ Criar lembretes manuais

### ADMIN
- ✅ Acesso total a todos os serviços

---

## Integração Kafka

### Tópicos

#### `agendamento-events`
- Recebe eventos de todas as operações de consultas
- Consumido por: Histórico, Notificações

#### `notificacao-topic`
- Eventos específicos de notificações
- Consumido por: Serviço de Notificações

### Estrutura de Eventos

```json
{
  "tipo": "CONSULTA_CRIADA",
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

## Exemplo de Fluxo Completo

1. **Médico cria uma consulta** via REST
   ```bash
   POST /api/v1/agenda/consultas
   Body: {
     "pacienteId": "PAC-1001",
     "medicoId": "MED-2001",
     "enfermeiroId": "ENF-3001",
     "dataHora": "2025-01-15T10:00:00",
     "duracaoMinutos": 60,
     "motivo": "Consulta cardiológica"
   }
   ```

2. **Agendamento publica evento** no Kafka
   - Evento `CONSULTA_CRIADA` é enviado para `agendamento-events` e `notificacao-topic`

3. **Serviço de Notificações consome evento**
   - Cria lembrete automático
   - Simula envio de notificação (email, SMS, push)

4. **Serviço de Histórico consome evento**
   - Armazena consulta no histórico
   - Disponibiliza via GraphQL

5. **Paciente consulta histórico** via GraphQL
   ```graphql
   query {
     pacienteConsultasFuturas(pacienteId: "PAC-1001") {
       id
       nomeMedico
       dataHoraFormatted
       status
     }
   }
   ```

---

## Benefícios da Arquitetura

✅ **Separação de Responsabilidades**: Cada serviço tem uma função específica
✅ **Escalabilidade**: Serviços podem ser escalados independentemente
✅ **Resiliência**: Falha em um serviço não derruba os outros
✅ **Flexibilidade**: GraphQL permite queries customizadas
✅ **Comunicação Assíncrona**: Kafka evita acoplamento entre serviços
✅ **Histórico Auditável**: Todos os eventos são registrados



