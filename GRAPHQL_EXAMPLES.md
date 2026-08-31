# GraphQL Query Examples

## 📋 Queries de Consultas do Paciente

### Listar todas as consultas de um paciente
```graphql
query {
  pacienteConsultas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    especialidade
    dataHoraFormatted
    descricao
    status
  }
}
```

### Listar apenas consultas futuras
```graphql
query {
  pacienteConsultasFuturas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    especialidade
    dataHoraFormatted
    status
  }
}
```

**Resultado esperado:**
```json
{
  "data": {
    "pacienteConsultasFuturas": [
      {
        "id": "1",
        "nomePaciente": "João Silva",
        "nomeMedico": "Dr. Carlos",
        "especialidade": "Cardiologia",
        "dataHoraFormatted": "15/01/2025 10:00",
        "status": "AGENDADA"
      }
    ]
  }
}
```

### Listar apenas consultas passadas
```graphql
query {
  pacienteConsultasPassadas(pacienteId: "PAC-1001") {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    descricao
    status
  }
}
```

---

## 🩺 Queries de Consultas do Médico

### Listar todas as consultas de um médico
```graphql
query {
  medicoConsultas(medicoId: "MED-2001") {
    id
    nomePaciente
    emailPaciente
    dataHoraFormatted
    descricao
    status
  }
}
```

**Resultado esperado:**
```json
{
  "data": {
    "medicoConsultas": [
      {
        "id": "1",
        "nomePaciente": "João Silva",
        "emailPaciente": "joao@example.com",
        "dataHoraFormatted": "15/01/2025 10:00",
        "descricao": "Consulta cardiológica",
        "status": "AGENDADA"
      },
      {
        "id": "3",
        "nomePaciente": "Maria Santos",
        "emailPaciente": "maria@example.com",
        "dataHoraFormatted": "17/01/2025 15:00",
        "descricao": "Retorno pós-cirurgia",
        "status": "AGENDADA"
      }
    ]
  }
}
```

---

## 🔍 Queries de Consulta Específica

### Buscar uma consulta pelo ID
```graphql
query {
  consulta(id: "1") {
    id
    nomePaciente
    nomeMedico
    especialidade
    dataHoraFormatted
    descricao
    tipoConsulta
    status
    criadaEm
    atualizadaEm
  }
}
```

**Resultado esperado:**
```json
{
  "data": {
    "consulta": {
      "id": "1",
      "nomePaciente": "João Silva",
      "nomeMedico": "Dr. Carlos",
      "especialidade": "Cardiologia",
      "dataHoraFormatted": "15/01/2025 10:00",
      "descricao": "Consulta cardiológica",
      "tipoConsulta": "Presencial",
      "status": "AGENDADA",
      "criadaEm": "14/01/2025 15:30",
      "atualizadaEm": "14/01/2025 15:30"
    }
  }
}
```

---

## 🔎 Queries com Filtros Avançados

### Filtrar consultas por paciente e status
```graphql
query {
  consultasFiltered(filtro: {
    pacienteId: "PAC-1001"
    status: AGENDADA
  }) {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
}
```

### Filtrar por período de datas
```graphql
query {
  consultasFiltered(filtro: {
    pacienteId: "PAC-1001"
    dataInicio: "01/01/2025"
    dataFim: "31/01/2025"
  }) {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
}
```

### Filtrar por médico e especialidade
```graphql
query {
  consultasFiltered(filtro: {
    medicoId: "MED-2001"
    especialidade: "Cardiologia"
  }) {
    id
    nomePaciente
    nomeMedico
    dataHoraFormatted
    status
  }
}
```

### Filtro completo
```graphql
query {
  consultasFiltered(filtro: {
    pacienteId: "PAC-1001"
    medicoId: "MED-2001"
    status: AGENDADA
    especialidade: "Cardiologia"
    dataInicio: "01/01/2025"
    dataFim: "31/12/2025"
  }) {
    id
    nomePaciente
    nomeMedico
    especialidade
    dataHoraFormatted
    descricao
    status
    criadaEm
  }
}
```

---

## ✏️ Mutations (Modificações)

### Cancelar uma consulta
```graphql
mutation {
  cancelarConsulta(id: "1") {
    sucesso
    mensagem
    consulta {
      id
      status
      nomePaciente
      nomeMedico
      dataHoraFormatted
    }
  }
}
```

**Resultado esperado (sucesso):**
```json
{
  "data": {
    "cancelarConsulta": {
      "sucesso": true,
      "mensagem": "Consulta 1 cancelada com sucesso",
      "consulta": {
        "id": "1",
        "status": "CANCELADA",
        "nomePaciente": "João Silva",
        "nomeMedico": "Dr. Carlos",
        "dataHoraFormatted": "15/01/2025 10:00"
      }
    }
  }
}
```

### Concluir uma consulta
```graphql
mutation {
  concluirConsulta(id: "1") {
    sucesso
    mensagem
    consulta {
      id
      status
      nomePaciente
      nomeMedico
      dataHoraFormatted
    }
  }
}
```

**Resultado esperado (sucesso):**
```json
{
  "data": {
    "concluirConsulta": {
      "sucesso": true,
      "mensagem": "Consulta 1 marcada como concluída",
      "consulta": {
        "id": "1",
        "status": "CONCLUIDA",
        "nomePaciente": "João Silva",
        "nomeMedico": "Dr. Carlos",
        "dataHoraFormatted": "15/01/2025 10:00"
      }
    }
  }
}
```

---

## 🚀 Testando com cURL

### Query simples
```bash
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { pacienteConsultasFuturas(pacienteId: \"PAC-1001\") { id nomePaciente nomeMedico dataHoraFormatted status } }"
  }'
```

### Query com autenticação JWT
```bash
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "query": "query { pacienteConsultasFuturas(pacienteId: \"PAC-1001\") { id nomePaciente nomeMedico dataHoraFormatted status } }"
  }'
```

### Mutation com cURL
```bash
curl -X POST http://localhost:8083/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "query": "mutation { cancelarConsulta(id: \"1\") { sucesso mensagem consulta { id status } } }"
  }'
```

---

## 🔐 Autenticação

Todas as queries GraphQL requerem autenticação JWT via header:

```
Authorization: Bearer YOUR_JWT_TOKEN
```

Exemplos de tokens por role:
- **PACIENTE**: Token com role ROLE_PACIENTE
- **MEDICO**: Token com role ROLE_MEDICO
- **ENFERMEIRO**: Token com role ROLE_ENFERMEIRO
- **ADMIN**: Token com role ROLE_ADMIN

---

## ⚠️ Mensagens de Erro

### Consulta não encontrada
```json
{
  "data": {
    "consulta": null
  }
}
```

### Acesso negado (Paciente consultando dados de outro)
```json
{
  "errors": [
    {
      "message": "Pacientes só podem consultar o próprio histórico.",
      "extensions": {
        "classification": "FORBIDDEN"
      }
    }
  ]
}
```

### Filtro inválido
```json
{
  "errors": [
    {
      "message": "Invalid input: Campo de data em formato inválido",
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ]
}
```

---

## 📊 Tipos Disponíveis

### StatusConsulta (Enum)
```graphql
AGENDADA    # Consulta agendada
CONFIRMADA  # Consulta confirmada pelo médico/enfermeiro
CONCLUIDA   # Consulta realizada
CANCELADA   # Consulta cancelada
REMARCADA   # Consulta remarcada
```

### ConsultaFiltro (Input)
```graphql
input ConsultaFiltro {
  pacienteId: ID          # Filtra por paciente
  medicoId: ID            # Filtra por médico
  status: StatusConsulta  # Filtra por status
  dataInicio: String      # Data inicial (formato: dd/MM/yyyy)
  dataFim: String         # Data final (formato: dd/MM/yyyy)
  especialidade: String   # Filtra por especialidade
}
```

### ConsultaResult (Result)
```graphql
type ConsultaResult {
  sucesso: Boolean!       # Se a operação foi bem-sucedida
  mensagem: String!       # Mensagem descritiva
  consulta: Consulta      # Consulta afetada pela operação
}
```

---

## 💡 Dicas de Uso

1. **Para performance**: Use apenas os campos que você precisa nas queries
2. **Para depuração**: Use a interface GraphiQL em http://localhost:8083/graphiql
3. **Para automação**: Salve suas queries em arquivos `.graphql`
4. **Para segurança**: Sempre inclua o token JWT no header Authorization
5. **Para logging**: Verifique os logs do serviço para debug de eventos Kafka

