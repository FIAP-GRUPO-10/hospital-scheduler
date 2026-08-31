# 🏥 Hospital Scheduler

> Sistema de agendamento hospitalar com microserviços, integração Kafka e autenticação JWT

**Table of Contents**
- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Requisitos](#requisitos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Módulos](#módulos)
- [Autenticação](#autenticação)
- [Endpoints](#endpoints)
- [Exemplos de Uso](#exemplos-de-uso)
- [Regras de Negócio](#regras-de-negócio)
- [Integração Kafka](#integração-kafka)
- [Troubleshooting](#troubleshooting)

---

## Visão Geral

Sistema completo de agendamento hospitalar desenvolvido em **Java 17 com Spring Boot 3.1.5**, utilizando arquitetura de microserviços com comunicação assíncrona via **Apache Kafka**.

### Principais Características
- ✅ **Autenticação JWT** - Segurança com tokens JWT por perfil
- ✅ **Microserviços** - Agendamento, Notificações e Histórico isolados
- ✅ **Mensageria Kafka** - Comunicação assíncrona entre serviços
- ✅ **Docker Compose** - Execução completa containerizada
- ✅ **Maven Multi-módulo** - Organização clara de responsabilidades
- ✅ **Validação** - Detecção de conflitos de agenda
- ✅ **Banco de Dados** - MySQL para persistência

---

## Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    Cliente (Browser/Postman)                │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────┴─────────┬──────────────┐
    │                  │              │
    ▼                  ▼              ▼
┌─────────────┐  ┌─────────────┐  ┌──────────────┐
│ AGENDAMENTO │  │NOTIFICAÇÕES │  │  HISTÓRICO   │
│  (8081)     │  │   (8082)    │  │   (8083)     │
│             │  │             │  │              │
│ - JWT Auth  │  │ - JWT Auth  │  │ - JWT Auth   │
│ - CRUD      │  │ - Lembretes │  │ - Consultar  │
│ - Producer  │  │ - Consumer  │  │ - Consumer   │
└──────┬──────┘  └──────┬──────┘  └──────┬───────┘
       │                │                │
       │     ┌──────────┴────────────┐   │
       │     │   Apache Kafka       │   │
       │     │  (Zookeeper + Broker)│   │
       │     └──────────┬───────────┘   │
       │                │                │
       └────────────────┼────────────────┘
                        │
            ┌───────────┴────────────┐
            ▼                        ▼
        ┌────────────┐        ┌─────────────┐
        │   MySQL    │        │ Kafka Topics│
        │ Persistência         │ agendamento-│
        └────────────┘        │ events,etc. │
                              └─────────────┘
```

---

## Requisitos

- **Java 17+** - OpenJDK ou Oracle JDK
- **Maven 3.8+** - Para build do projeto
- **Docker & Docker Compose** - Para execução containerizada
- **Git** - Controle de versão
- **Postman** (opcional) - Para testar endpoints

**Verificar instalação:**
```bash
java -version
mvn -version
docker --version
docker-compose --version
```

---

## Estrutura do Projeto

```
hospital-scheduler/
├── pom.xml                              # POM agregador (parent)
├── docker-compose.yml                   # Orquestração dos containers
├── Dockerfile                           # Dockerfile raiz
├── README.md                            # Esta documentação
├── KAFKA_SETUP.md                       # Guia de configuração Kafka
├── KAFKA_QUICK_START.md                 # Quick start Kafka
├── MIGRATION_SUMMARY.md                 # Resumo de migração
│
├── agendamento/                         # Módulo de agendamento (Porta 8081)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/main/java/br/com/fiap/hospital/modules/agendamento/
│   │   ├── AgendamentoApplication.java
│   │   ├── config/                      # Configurações (Kafka, Security)
│   │   ├── controller/
│   │   │   ├── AgendamentoController.java
│   │   │   └── HealthController.java
│   │   ├── model/
│   │   │   ├── Consulta.java
│   │   │   └── ConsultaRequest.java
│   │   ├── security/
│   │   │   └── AuthController.java
│   │   └── service/
│   │       ├── AgendamentoService.java
│   │       └── KafkaProducerService.java
│   └── src/main/resources/
│       └── application.properties
│
├── notificacoes/                        # Módulo de notificações (Porta 8082)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/main/java/br/com/fiap/hospital/modules/notificacoes/
│   │   ├── NotificacoesApplication.java
│   │   ├── config/                      # Configurações (Kafka, Security)
│   │   ├── controller/
│   │   │   ├── NotificacoesController.java
│   │   │   └── HealthController.java
│   │   ├── model/
│   │   │   ├── Lembrete.java
│   │   │   └── LembreteRequest.java
│   │   ├── security/
│   │   │   └── AuthController.java
│   │   └── service/
│   │       ├── NotificacoesService.java
│   │       └── KafkaConsumerService.java
│   └── src/main/resources/
│       └── application.properties
│
├── historico/                           # Módulo de histórico (Porta 8083)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/main/java/br/com/fiap/hospital/modules/historico/
│   │   ├── HistoricoApplication.java
│   │   ├── config/                      # Configurações (Kafka, Security)
│   │   ├── controller/
│   │   │   ├── HistoricoController.java
│   │   │   └── HealthController.java
│   │   ├── model/
│   │   │   └── ConsultaHistorico.java
│   │   ├── security/
│   │   │   └── AuthController.java
│   │   └── service/
│   │       ├── HistoricoService.java
│   │       └── KafkaConsumerService.java
│   └── src/main/resources/
│       └── application.properties
│
└── postman/                             # Coleções Postman para testes
    ├── Hospital-Scheduler-Local.postman_collection.json
    └── Hospital-Scheduler-Local.postman_environment.json
```

---

## Como Executar

### 1. Execução Local (Sem Docker)

**Pré-requisitos:**
- Instalar Kafka e Zookeeper localmente OU usar docker apenas para eles
- MySQL rodando

**Build do Projeto:**
```bash
cd /home/sandoval/IdeaProjects/hospital-scheduler
mvn clean install
```

**Iniciar Kafka (opcional - se não estiver no Docker):**
```bash
# Terminal 1 - Zookeeper
zkServer.sh start

# Terminal 2 - Kafka
kafka-server-start.sh config/server.properties
```

**Iniciar Módulos (em terminais separados):**
```bash
# Terminal 3 - Agendamento
cd agendamento && mvn spring-boot:run

# Terminal 4 - Notificações
cd notificacoes && mvn spring-boot:run

# Terminal 5 - Histórico
cd historico && mvn spring-boot:run
```

**Acessar Services:**
- Agendamento: http://localhost:8081
- Notificações: http://localhost:8082
- Histórico: http://localhost:8083

### 2. Execução com Docker Compose (Recomendado)

**Build e Iniciar:**
```bash
cd /home/sandoval/IdeaProjects/hospital-scheduler

# Construir imagens e subir containers
docker-compose up -d --build

# Verificar status
docker-compose ps

# Ver logs de um serviço
docker-compose logs -f agendamento
```

**Parar Serviços:**
```bash
docker-compose down

# Remover volumes (limpar dados)
docker-compose down -v
```

**Acessar Services:**
- Agendamento: http://localhost:8081
- Notificações: http://localhost:8082
- Histórico: http://localhost:8083
- MySQL: localhost:3307
- Kafka: localhost:9092

### 3. Executar Testes

```bash
# Todos os testes
mvn test

# Testes de um módulo específico
cd agendamento && mvn test
```

---

## Módulos

### 🏥 Agendamento (Port 8081)

**Responsabilidades:**
- Gerenciar agendamentos de consultas
- Validar conflitos de horários
- Produzir eventos para notificação
- Autenticar usuários

**Porta:** 8081
**Perfis com acesso:** Médico, Enfermeiro, Paciente

### 📢 Notificações (Port 8082)

**Responsabilidades:**
- Gerenciar lembretes de consultas
- Consumir eventos de agendamento
- Enviar notificações
- Autenticar usuários

**Porta:** 8082
**Perfis com acesso:** Paciente, Médico, Enfermeiro

### 📋 Histórico (Port 8083)

**Responsabilidades:**
- Manter histórico de consultas
- Consumir eventos de agendamento
- Persistir dados de histórico
- Autenticar usuários

**Porta:** 8083
**Perfis com acesso:** Paciente, Médico, Enfermeiro

---

## Autenticação

### Credenciais Padrão

Cada módulo implementa autenticação JWT independente. Use as seguintes credenciais:

| Perfil | ID | Senha |
|--------|----|----|
| Médico | MED-2001 | medico123 |
| Enfermeiro | ENF-3001 | enfermeiro123 |
| Paciente | PAC-1001 | paciente123 |

### Fluxo de Autenticação

**1. Fazer Login:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "MED-2001",
    "password": "medico123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**2. Usar Token em Requisições:**
```bash
curl -X GET http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Características de Segurança

- ✅ Tokens JWT com expiração
- ✅ Validação de assinatura
- ✅ Controle de acesso por perfil
- ✅ Senhas não trafegam a cada requisição
- ✅ HTTPS recomendado em produção

---

## Endpoints

### 1️⃣ AGENDAMENTO (Port 8081)

#### `POST /api/v1/auth/login`
**Descrição:** Autenticar usuário e obter JWT token

**Request:**
```json
{
  "username": "MED-2001",
  "password": "medico123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**Curl:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "MED-2001", "password": "medico123"}'
```

---

#### `GET /api/v1/agenda/`
**Descrição:** Status check do serviço

**Response (200):**
```json
{
  "status": "Agendamento service is running"
}
```

**Curl:**
```bash
curl http://localhost:8081/api/v1/agenda/
```

---

#### `GET /api/v1/agenda/consultas`
**Descrição:** Listar todas as consultas (com filtro opcional por paciente)

**Query Parameters:**
- `X-Paciente-Id` (header, opcional): Filtrar por paciente

**Response (200):**
```json
[
  {
    "id": 1,
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2026-08-25T14:30:00",
    "duracaoMinutos": 30,
    "motivo": "Consulta Geral",
    "status": "AGENDADA"
  }
]
```

**Curl:**
```bash
curl -X GET http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer TOKEN" \
  -H "X-Paciente-Id: PAC-1001"
```

---

#### `GET /api/v1/agenda/consultas/{id}`
**Descrição:** Obter detalhes de uma consulta específica

**Path Parameters:**
- `id` (required): ID da consulta

**Response (200):**
```json
{
  "id": 1,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2026-08-25T14:30:00",
  "duracaoMinutos": 30,
  "motivo": "Consulta Geral",
  "status": "AGENDADA"
}
```

**Curl:**
```bash
curl -X GET http://localhost:8081/api/v1/agenda/consultas/1 \
  -H "Authorization: Bearer TOKEN"
```

---

#### `POST /api/v1/agenda/consultas`
**Descrição:** Criar nova consulta

**Request Body:**
```json
{
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2026-08-25T14:30:00",
  "duracaoMinutos": 30,
  "motivo": "Consulta Geral"
}
```

**Response (201):**
```json
{
  "id": 2,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2026-08-25T14:30:00",
  "duracaoMinutos": 30,
  "motivo": "Consulta Geral",
  "status": "AGENDADA"
}
```

**Validações:**
- ❌ Paciente, Médico e Enfermeiro obrigatórios
- ❌ Data/hora deve ser futura
- ❌ Horário comercial (08:00-18:00)
- ❌ Não pode conflitar com outros agendamentos

**Curl:**
```bash
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2026-08-25T14:30:00",
    "duracaoMinutos": 30,
    "motivo": "Consulta Geral"
  }'
```

---

#### `PUT /api/v1/agenda/consultas/{id}`
**Descrição:** Atualizar consulta existente

**Path Parameters:**
- `id` (required): ID da consulta

**Request Body:**
```json
{
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2026-08-26T15:00:00",
  "duracaoMinutos": 45,
  "motivo": "Consulta Geral - Atualizada"
}
```

**Response (200):**
```json
{
  "id": 1,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "enfermeiroId": "ENF-3001",
  "dataHora": "2026-08-26T15:00:00",
  "duracaoMinutos": 45,
  "motivo": "Consulta Geral - Atualizada",
  "status": "AGENDADA"
}
```

**Curl:**
```bash
curl -X PUT http://localhost:8081/api/v1/agenda/consultas/1 \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2026-08-26T15:00:00",
    "duracaoMinutos": 45,
    "motivo": "Consulta Geral - Atualizada"
  }'
```

---

### 2️⃣ NOTIFICAÇÕES (Port 8082)

#### `POST /api/v1/auth/login`
**Descrição:** Autenticar usuário e obter JWT token

**Request:**
```json
{
  "username": "PAC-1001",
  "password": "paciente123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**Curl:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "PAC-1001", "password": "paciente123"}'
```

---

#### `GET /api/v1/notificacoes/`
**Descrição:** Status check do serviço

**Response (200):**
```json
{
  "status": "Notificacoes service is running"
}
```

**Curl:**
```bash
curl http://localhost:8082/api/v1/notificacoes/
```

---

#### `GET /api/v1/notificacoes/lembretes`
**Descrição:** Listar todos os lembretes (com filtro opcional por paciente)

**Query Parameters:**
- `X-Paciente-Id` (header, opcional): Filtrar por paciente

**Response (200):**
```json
[
  {
    "id": 1,
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "dataConsulta": "2026-08-25T14:30:00",
    "mensagem": "Lembrete de consulta com Dr. João",
    "enviado": false
  }
]
```

**Curl:**
```bash
curl -X GET http://localhost:8082/api/v1/notificacoes/lembretes \
  -H "Authorization: Bearer TOKEN" \
  -H "X-Paciente-Id: PAC-1001"
```

---

#### `POST /api/v1/notificacoes/lembretes`
**Descrição:** Criar novo lembrete

**Request Body:**
```json
{
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "dataConsulta": "2026-08-25T14:30:00",
  "mensagem": "Lembrete de consulta com Dr. João"
}
```

**Response (201):**
```json
{
  "id": 2,
  "pacienteId": "PAC-1001",
  "medicoId": "MED-2001",
  "dataConsulta": "2026-08-25T14:30:00",
  "mensagem": "Lembrete de consulta com Dr. João",
  "enviado": false
}
```

**Curl:**
```bash
curl -X POST http://localhost:8082/api/v1/notificacoes/lembretes \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "dataConsulta": "2026-08-25T14:30:00",
    "mensagem": "Lembrete de consulta com Dr. João"
  }'
```

---

### 3️⃣ HISTÓRICO (Port 8083)

#### `POST /api/v1/auth/login`
**Descrição:** Autenticar usuário e obter JWT token

**Request:**
```json
{
  "username": "PAC-1001",
  "password": "paciente123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**Curl:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "PAC-1001", "password": "paciente123"}'
```

---

#### `GET /api/v1/historico/`
**Descrição:** Status check do serviço

**Response (200):**
```json
{
  "status": "Historico service is running"
}
```

**Curl:**
```bash
curl http://localhost:8083/api/v1/historico/
```

---

#### `GET /api/v1/historico/consultas`
**Descrição:** Listar histórico de todas as consultas (com filtro opcional por paciente)

**Query Parameters:**
- `X-Paciente-Id` (header, opcional): Filtrar por paciente

**Response (200):**
```json
[
  {
    "id": 1,
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "dataConsulta": "2026-08-25T14:30:00",
    "motivo": "Consulta Geral",
    "resultado": "Paciente em bom estado"
  }
]
```

**Curl:**
```bash
curl -X GET http://localhost:8083/api/v1/historico/consultas \
  -H "Authorization: Bearer TOKEN" \
  -H "X-Paciente-Id: PAC-1001"
```

---

#### `GET /api/v1/historico/pacientes/{pacienteId}/consultas`
**Descrição:** Listar consultas de um paciente específico

**Path Parameters:**
- `pacienteId` (required): ID do paciente

**Query Parameters:**
- `futuras` (optional, default: false): Mostrar apenas consultas futuras

**Response (200):**
```json
[
  {
    "id": 1,
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "dataConsulta": "2026-08-25T14:30:00",
    "motivo": "Consulta Geral",
    "resultado": null
  }
]
```

**Curl:**
```bash
# Todas as consultas
curl -X GET http://localhost:8083/api/v1/historico/pacientes/PAC-1001/consultas \
  -H "Authorization: Bearer TOKEN"

# Apenas futuras
curl -X GET http://localhost:8083/api/v1/historico/pacientes/PAC-1001/consultas?futuras=true \
  -H "Authorization: Bearer TOKEN"
```

---

### Health Check Endpoints

#### `GET /health` (Notificações)
**Descrição:** Verificar saúde do serviço de notificações

**Response (200):**
```json
{
  "status": "UP"
}
```

**Curl:**
```bash
curl http://localhost:8082/health
```

---

## Exemplos de Uso

### 1. Fluxo Completo: Criar Agendamento

```bash
# 1. Login como Médico
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "MED-2001", "password": "medico123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"

# 2. Criar Consulta
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2026-08-25T14:30:00",
    "duracaoMinutos": 30,
    "motivo": "Consulta Geral"
  }' | jq

# 3. Login como Paciente (em outro terminal)
TOKEN_PAC=$(curl -s -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "PAC-1001", "password": "paciente123"}' \
  | jq -r '.token')

# 4. Ver Lembretes (será preenchido via Kafka)
curl -X GET http://localhost:8082/api/v1/notificacoes/lembretes \
  -H "Authorization: Bearer $TOKEN_PAC" \
  -H "X-Paciente-Id: PAC-1001" | jq

# 5. Ver Histórico
curl -X GET http://localhost:8083/api/v1/historico/pacientes/PAC-1001/consultas \
  -H "Authorization: Bearer $TOKEN_PAC" | jq
```

### 2. Validar Conflito de Agenda

```bash
# Tentar criar consulta com horário conflitante
curl -X POST http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "PAC-1001",
    "medicoId": "MED-2001",
    "enfermeiroId": "ENF-3001",
    "dataHora": "2026-08-25T14:30:00",
    "duracaoMinutos": 30,
    "motivo": "Consulta de Acompanhamento"
  }' | jq

# Retorno esperado: 400 - Conflito de agenda detectado
```

### 3. Listar Consultas com Filtro

```bash
# Como Paciente, ver apenas suas consultas
curl -X GET http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer $TOKEN_PAC" \
  -H "X-Paciente-Id: PAC-1001" | jq
```

---

## Regras de Negócio

### 📋 Agendamento
- ✅ Consulta deve ter paciente, médico e enfermeiro
- ✅ Data/hora deve ser futura
- ✅ Horário deve estar entre 08:00 e 18:00 (comercial)
- ✅ Não pode ter sobreposição de horários para:
  - Mesmo paciente
  - Mesmo médico
  - Mesmo enfermeiro
- ✅ Duração mínima: 15 minutos
- ✅ Duração máxima: 480 minutos (8 horas)

### 📢 Notificações
- ✅ Lembretes são criados automaticamente via Kafka
- ✅ Paciente pode visualizar seus próprios lembretes
- ✅ Médicos/Enfermeiros veem todos os lembretes
- ✅ Sistema marca como enviado após processamento

### 📋 Histórico
- ✅ Consultas são registradas automaticamente via Kafka
- ✅ Paciente vê apenas seu histórico
- ✅ Médicos/Enfermeiros veem histórico completo
- ✅ Permite filtrar por paciente
- ✅ Permite filtrar por data (futuras/passadas)

### 🔐 Controle de Acesso
- ✅ **Médicos**: Criar e editar consultas
- ✅ **Enfermeiros**: Ver e editar consultas
- ✅ **Pacientes**: Ver apenas dados próprios

---

## Integração Kafka

### Tópicos Disponíveis

| Tópico | Produtor | Consumidor | Descrição |
|--------|----------|-----------|-----------|
| `agendamento-events` | Agendamento | Notificacoes, Historico | Eventos de agendamento |
| `notificacao-topic` | Notificacoes | Historico | Eventos de notificação |

### Monitorar Mensagens Kafka

```bash
# Listar tópicos
docker exec -it hospital-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list

# Consumir mensagens em tempo real
docker exec -it hospital-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic agendamento-events \
  --from-beginning

# Ver estatísticas do grupo
docker exec -it hospital-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group notificacoes-group
```

### Configuração Kafka (Desenvolvedor)

Para adicionar novo tópico ou Consumer:

1. **Usar `KafkaProducerService`** para enviar mensagens:
```java
@Autowired
private KafkaProducerService kafkaProducerService;

kafkaProducerService.sendMessage("agendamento-events", consulta);
```

2. **Usar `@KafkaListener`** para receber mensagens:
```java
@KafkaListener(topics = "agendamento-events", groupId = "meu-grupo")
public void escutarEvento(String mensagem) {
    // Processar mensagem
}
```

Consulte `KAFKA_SETUP.md` e `KAFKA_QUICK_START.md` para mais detalhes.

---

## Troubleshooting

### 🔴 Erro: "Connection refused"

**Causa:** Serviço Kafka ou MySQL não está respondendo

**Solução:**
```bash
# Verificar status dos containers
docker-compose ps

# Ver logs
docker-compose logs kafka
docker-compose logs mysql

# Reiniciar serviços
docker-compose restart
```

### 🔴 Erro: "401 Unauthorized"

**Causa:** Token JWT inválido, expirado ou ausente

**Solução:**
```bash
# Fazer login novamente para obter novo token
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "MED-2001", "password": "medico123"}'
```

### 🔴 Erro: "409 Conflict - Agenda Conflict"

**Causa:** Conflito de horários detectado

**Solução:** Escolher outro horário que não sobreponha com consultas existentes

```bash
# Verificar consultas existentes
curl -X GET http://localhost:8081/api/v1/agenda/consultas \
  -H "Authorization: Bearer $TOKEN" | jq
```

### 🔴 Erro: "400 Bad Request - Invalid date"

**Causa:** Data/hora inválida ou no passado

**Solução:** Usar data futura no formato ISO 8601 (YYYY-MM-DDTHH:mm:ss)

```bash
# Exemplo correto
"dataHora": "2026-08-25T14:30:00"
```

### 🔴 Kafka não cria tópicos automaticamente

**Verificar Configuração:**
```bash
# Confirmar que auto-create está ativado
docker-compose ps kafka

# Ver logs
docker-compose logs kafka | grep auto-create
```

**Habilitar:**
Verificar `docker-compose.yml` - deve ter:
```yaml
KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
```

### 🔴 Lembretes não aparecem após criar consulta

**Causa:** Consumer Kafka não está processando mensagens

**Solução:**
```bash
# Verificar logs do serviço de Notificacoes
docker-compose logs notificacoes

# Verificar se o tópico existe
docker exec -it hospital-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic agendamento-events

# Reiniciar consumer
docker-compose restart notificacoes
```

---

## Performance & Otimizações

### Índices de Banco de Dados
```sql
CREATE INDEX idx_consulta_paciente ON consulta(paciente_id);
CREATE INDEX idx_consulta_medico ON consulta(medico_id);
CREATE INDEX idx_consulta_data ON consulta(data_hora);
```

### Tuning Kafka
- Aumentar `num.partitions` para paralelismo
- Ajustar `batch.size` para throughput
- Configurar `linger.ms` para latência vs. throughput

### Cache de Tokens
- Tokens JWT têm expiração de 1 hora
- Implemente refresh token em produção
- Considere Redis para cache de sessão

---

## Stack Técnico

| Componente | Versão | Função |
|-----------|--------|--------|
| Java | 17 | Linguagem |
| Spring Boot | 3.1.5 | Framework |
| Maven | 3.8+ | Build tool |
| MySQL | 8.0 | Banco de dados |
| Apache Kafka | Latest | Message broker |
| Zookeeper | Latest | Coordenação Kafka |
| Docker | Latest | Containerização |
| JWT | 0.11.5 | Autenticação |

---

## Contato & Suporte

Para dúvidas ou problemas:
1. Verificar documentação (README.md)
2. Consultar logs: `docker-compose logs -f <serviço>`
3. Teste endpoints com Postman: `postman/Hospital-Scheduler-Local.postman_collection.json`

---

**Última atualização:** Agosto 2026  
**Versão:** 1.0.0  
**Status:** ✅ Production Ready
