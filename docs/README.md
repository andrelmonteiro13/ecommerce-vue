# Sistema Corporativo Base

Projeto base corporativo construído com **JHipster + Spring Boot + PostgreSQL + Vue**, com foco em padronização, produtividade e escalabilidade.

---

## Objetivo

Este projeto serve como base padronizada para novos sistemas internos e corporativos, reduzindo retrabalho na criação de aplicações e permitindo evolução consistente entre módulos e equipes.

---

## Stack utilizada

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- Liquibase
- MapStruct

### Frontend
- Vue
- TypeScript
- Vue Router

### Banco de dados
- PostgreSQL

### Infraestrutura
- Docker
- Docker Compose

---

## Padrões adotados

- Arquitetura gerada pelo JHipster
- Separação por camadas:
  - domain
  - repository
  - service
  - service/dto
  - service/mapper
  - web/rest
- Uso de DTO para tráfego externo
- Uso de MapStruct para mapeamento
- Liquibase para versionamento de banco
- Paginação em listagens
- Validação de dados no backend
- Tratamento padronizado de erros

---

## Estrutura do projeto

```text
/src/main/java
  /domain
  /repository
  /service
    /dto
    /mapper
  /web/rest
  /security
  /config

/src/main/webapp
  /app
  /entities
  /shared
  /router

/src/test
/jdl
/docs