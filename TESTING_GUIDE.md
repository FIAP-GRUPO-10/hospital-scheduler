# 🧪 Hospital Scheduler - Guia de Testes

## Status: ✅ BUILD SUCESSO

Todos os serviços foram compilados e empacotados com sucesso!

```
✓ agendamento-1.0.0.jar
✓ notificacoes-1.0.0.jar  
✓ historico-1.0.0.jar
```

---

## 📋 Roteiro de Teste

### Fase 1: Preparação (5 min)

#### 1.1 Iniciar Kafka & Zookeeper

```bash
# Opção A: Docker Compose (Recomendado)
docker-compose up -d kafka zookeeper

# Opção B: Kafka Local
cd /path/to/kafka
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
```

Verificar que está rodando:
```bash
docker ps | grep kafka
curl localhost:9092
```

#### 1.2 Criar Tópicos Kafka (se necessário)

```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic agendamento-events
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic notificacao-topic
```

---

### Fase 2: Executar Serviços (Simultâneamente)

Abra 3 terminais separados:

#### Terminal 1: Serviço de Agendamento

```bash
cd /home/sandoval/IdeaProjects/hospital-scheduler/agendamento
java -jar target/agendamento-1.0.0.jar
```

Esperado:
```
Started AgendamentoApplication
Tomcat started on port(s): 8081
```

#### Terminal 2: Serviço de Notificações

```bash
cd /home/sandoval/IdeaProjects/hospital-scheduler/notificacoes
java -jar target/notificacoes-1.0.0.jar
```

Esperado:
```
Started NotificacoesApplication
Tomcat started on port(s): 8082
Listening to Kafka topics: agendamento-events, notificacao-topic
```

#### Terminal 3: Serviço de Histórico

```bash
cd /home/sandoval/IdeaProjects/hospital-scheduler/historico
java -jar target/historico-1.0.0.jar
```

Esperado:
```
Started HistoricoApplication
Tomcat started on port(s): 8083
GraphQL endpoint available at: /graphql
GraphiQL UI available at: /graphiql
```

---

### Fase 3: Teste REST API (5 min)

#### 3.1 Status dos Serviços

```bash
# Agendamento
curl -v http://localhost:8081/api/v1/agenda

# Notificações
curl -v http://localhost:8082/api/v1/notificacoes

# Histórico
curl -v http://localhost:8083/api/v1/historico
```

Esperado: **HTTP 200 OK** em todos

#### 3.2 Criar Consulta

```bash
# Criar consulta com todos os campos obrigatórios
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2025-01-20T10:00:00",
    "duracaoMinutos": 60,
    "motivo": "Consulta cardiológica"
  }'
```

Esperado:
```json
{
  "id": 3,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "status": "AGENDADA",
  ...
}
```

#### 3.3 Listar Consultas

```bash
curl http://localhost:8081/api/v1/agenda/consultas
```

Esperado: Array com todas as consultas (incluindo a nova)

#### 3.4 Buscar Consulta Específica

```bash
curl http://localhost:8081/api/v1/agenda/consultas/1
```

Esperado: Dados da consulta com ID 1

#### 3.5 Editar Consulta

```bash
curl -X PUT http://localhost:8081/api/v1/agenda/consultas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2002",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2025-01-21T14:00:00",
    "duracaoMinutos": 45,
    "motivo": "Acompanhamento clínico"
  }'
```

Esperado: Consulta atualizada com novos dados

#### 3.6 Confirmar Consulta

```bash
curl -X PUT http://localhost:8081/api/v1/agenda/consultas/1/confirmar
```

Esperado: Consulta com status "CONFIRMADA"

#### 3.7 Cancelar Consulta

```bash
curl -X PUT http://localhost:8081/api/v1/agenda/consultas/2/cancelar
```

Esperado: Consulta com status "CANCELADA"

#### 3.8 Deletar Consulta

```bash
curl -X DELETE http://localhost:8081/api/v1/agenda/consultas/3
```

Esperado: HTTP 200 com mensagem de sucesso

---

### Fase 4: Teste GraphQL (5 min)

#### 4.1 Abrir GraphiQL

Abra no navegador:
```
http://localhost:8083/graphiql
```

#### 4.2 Testar Query: Listar Todas as Consultas

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
```

Esperado:
```json
{
  "data": {
    "pacienteConsultas": [
      {
        "id": "1",
        "nomePaciente": "João Silva",
        "nomeMedico": "Dr. Carlos",
        "dataHoraFormatted": "15/01/2025 10:00",
        "status": "AGENDADA"
      },
      ...
    ]
  }
}
```

#### 4.3 Testar Query: Consultas Futuras

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

Esperado: Apenas consultas com data no futuro

#### 4.4 Testar Query: Consultas Passadas

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

Esperado: Apenas consultas com data no passado

#### 4.5 Testar Query: Consultas do Médico

```graphql
query {
  medicoConsultas(medicoId: "MED-2001") {
    id
    nomePaciente
    dataHoraFormatted
    status
  }
}
```

Esperado: Todas as consultas do médico MED-2001

#### 4.6 Testar Query: Filtros Avançados

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

Esperado: Apenas consultas agendadas do paciente

#### 4.7 Testar Mutation: Cancelar

```graphql
mutation {
  cancelarConsulta(id: "2") {
    sucesso
    mensagem
    consulta {
      id
      status
    }
  }
}
```

Esperado:
```json
{
  "data": {
    "cancelarConsulta": {
      "sucesso": true,
      "mensagem": "Consulta 2 cancelada com sucesso",
      "consulta": {
        "id": "2",
        "status": "CANCELADA"
      }
    }
  }
}
```

#### 4.8 Testar Mutation: Concluir

```graphql
mutation {
  concluirConsulta(id: "3") {
    sucesso
    mensagem
    consulta {
      id
      status
    }
  }
}
```

Esperado: Status alterado para CONCLUIDA

---

### Fase 5: Teste Kafka & Notificações (5 min)

#### 5.1 Monitorar Tópicos Kafka

Em um novo terminal:
```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic agendamento-events --from-beginning
```

#### 5.2 Criar Nova Consulta

```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1002",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2025-02-01T10:00:00",
    "duracaoMinutos": 60,
    "motivo": "Consulta de retorno"
  }'
```

#### 5.3 Verificar Eventos no Kafka

Nos logs do consumer Kafka, você deve ver:
```json
{
  "tipo": "CONSULTA_CRIADA",
  "consultaId": 4,
  "pacienteId": "PAC-1002",
  ...
}
```

#### 5.4 Verificar Logs de Notificação

No terminal do serviço de Notificações, você deve ver:
```
=== ENVIANDO NOTIFICAÇÃO ===
Para: PAC-1002
Médico: MED-2001
Data da Consulta: 01/02/2025 10:00
Mensagem: Sua consulta foi agendada para 01/02/2025 10:00 com o médico ID: MED-2001. Motivo: Consulta de retorno
===========================
```

---

### Fase 6: Teste de Validações (3 min)

#### 6.1 Tentar criar consulta em horário inválido

```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2025-01-15T22:00:00",
    "duracaoMinutos": 60,
    "motivo": "Teste"
  }'
```

Esperado: **HTTP 400** com erro "As consultas devem ocorrer entre 08:00 e 18:00"

#### 6.2 Tentar criar consulta no passado

```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2020-01-15T10:00:00",
    "duracaoMinutos": 60,
    "motivo": "Teste"
  }'
```

Esperado: **HTTP 400** com erro "A consulta deve ser agendada para uma data futura"

#### 6.3 Tentar criar consulta com campos faltando

```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001"
  }'
```

Esperado: **HTTP 400** com erro "Enfermeiro obrigatório" e outros campos

#### 6.4 Tentar detectar conflito

Criar duas consultas com mesmo médico, paciente ou enfermeiro no mesmo horário:

```bash
# Primeira
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-TEST",
    "medicoId": "MED-TEST",
    "enfermeiroId": "ENF-TEST",
    "dataHora": "2025-03-01T10:00:00",
    "duracaoMinutos": 60,
    "motivo": "Teste 1"
  }'

# Segunda (mesma hora, mesmo médico)
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-OUTRO",
    "medicoId": "MED-TEST",
    "enfermeiroId": "ENF-OUTRO",
    "dataHora": "2025-03-01T10:30:00",
    "duracaoMinutos": 30,
    "motivo": "Teste 2"
  }'
```

Esperado: **HTTP 400** com erro "Já existe conflito de agenda"

---

### Fase 7: Verificação Visual (2 min)

#### 7.1 GraphQL Schema

Abra GraphiQL e clique em "Docs" (lado direito) para ver o schema completo

#### 7.2 Tipos Disponíveis

Você deve ver:
- Query type (com 6 queries)
- Mutation type (com 2 mutations)
- Tipo Consulta (com ~13 campos)
- Tipo ConsultaResult
- Tipo ConsultaFiltro
- Enum StatusConsulta

---

## 📊 Checklist de Testes

### REST API
- [ ] GET `/api/v1/agenda` - Status OK
- [ ] GET `/api/v1/agenda/consultas` - Listagem funciona
- [ ] POST `/api/v1/agenda/consultas` - Criação funciona
- [ ] PUT `/api/v1/agenda/consultas/{id}` - Edição funciona
- [ ] DELETE `/api/v1/agenda/consultas/{id}` - Deleção funciona
- [ ] PUT `/api/v1/agenda/consultas/{id}/cancelar` - Cancelamento funciona
- [ ] PUT `/api/v1/agenda/consultas/{id}/confirmar` - Confirmação funciona

### GraphQL
- [ ] Query `pacienteConsultas` funciona
- [ ] Query `pacienteConsultasFuturas` funciona
- [ ] Query `pacienteConsultasPassadas` funciona
- [ ] Query `medicoConsultas` funciona
- [ ] Query `consulta` funciona
- [ ] Query `consultasFiltered` com filtros funciona
- [ ] Mutation `cancelarConsulta` funciona
- [ ] Mutation `concluirConsulta` funciona

### Validações
- [ ] Rejeita horário fora do intervalo 08:00-18:00
- [ ] Rejeita data no passado
- [ ] Rejeita campos obrigatórios faltando
- [ ] Detecta conflitos de agenda

### Kafka & Notificações
- [ ] Eventos são publicados quando consulta é criada
- [ ] Eventos são recebidos no Kafka
- [ ] Notificações são criadas automaticamente
- [ ] Lembretes aparecem nos logs

### Histórico
- [ ] Consultas aparecem no histórico
- [ ] Histórico persiste dados do Kafka
- [ ] GraphQL consegue recuperar dados do histórico

---

## 🔧 Troubleshooting

### "Connection refused" ao conectar em Kafka

```bash
# Verificar se Kafka está rodando
docker ps | grep kafka

# Se não estiver, iniciar
docker-compose up -d kafka zookeeper
```

### "Port 8081 already in use"

```bash
# Encontrar processo usando a porta
lsof -i :8081

# Matar processo
kill -9 <PID>

# Ou usar porta diferente
java -jar target/agendamento-1.0.0.jar --server.port=8085
```

### GraphQL retorna erro "schema not found"

```bash
# Verificar que schema.graphqls existe
ls historico/src/main/resources/graphql/

# Se não existir, criar diretório
mkdir -p historico/src/main/resources/graphql/
```

### Notificações não aparecem nos logs

```bash
# Verificar que Kafka está recebendo mensagens
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic agendamento-events \
  --from-beginning

# Verificar group id do consumer
docker logs notificacoes | grep "notificacoes-group"
```

---

## ✅ Resultado Esperado

Se todos os testes passarem, você terá:

1. ✅ **Serviço de Agendamento**: REST API completa funcionando
2. ✅ **Serviço de Notificações**: Lembretes automáticos via Kafka
3. ✅ **Serviço de Histórico**: GraphQL queries/mutations funcionando
4. ✅ **Integração Kafka**: Eventos fluindo entre serviços
5. ✅ **Validações**: Todas as regras de negócio sendo respeitadas
6. ✅ **Documentação**: Exemplos e arquitetura documentados

---

## 📞 Suporte

Se encontrar problemas:

1. Verificar logs de cada serviço
2. Consultar GRAPHQL_ARCHITECTURE.md
3. Consultar GRAPHQL_EXAMPLES.md
4. Consultar IMPLEMENTATION_SUMMARY.md

---

**Tempo estimado total de testes**: ~30 minutos

Boa sorte! 🚀

