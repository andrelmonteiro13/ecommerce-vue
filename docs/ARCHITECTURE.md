# ARCHITECTURE.md

# Arquitetura do Projeto

## Visão geral

Este projeto segue uma arquitetura padronizada baseada em **JHipster + Spring Boot + PostgreSQL + Vue**, com foco em:

- padronização entre projetos
- produtividade no desenvolvimento
- separação clara de responsabilidades
- facilidade de manutenção
- escalabilidade
- segurança
- compatibilidade com geração e evolução pelo JHipster

A regra principal deste repositório é:

> **não criar arquitetura paralela ao padrão do JHipster sem justificativa técnica clara**

---

## Stack principal

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- Liquibase
- MapStruct

### Frontend
- Vue
- TypeScript
- Vue Router
- componentes e estrutura gerados pelo JHipster

### Banco de dados
- PostgreSQL

### Infraestrutura
- Docker
- Docker Compose

---

## Estilo arquitetural adotado

A aplicação segue uma arquitetura em camadas, com responsabilidades separadas:

1. **domain**  
   Representa o modelo de domínio e as entidades persistidas.

2. **repository**  
   Responsável pelo acesso a dados.

3. **service**  
   Camada de regra de negócio e orquestração.

4. **service/dto**  
   Objetos de transferência de dados entre backend e frontend.

5. **service/mapper**  
   Conversão entre entidades e DTOs.

6. **web/rest**  
   Exposição dos endpoints REST.

7. **security**  
   Autenticação, autorização e regras de acesso.

8. **config**  
   Configurações gerais da aplicação.

9. **frontend (Vue)**  
   Interface do usuário, telas, rotas, formulários, listagens e integração com API.

---

## Estrutura esperada do backend

```text
src/main/java/br/com/seuprojeto/app
  /config
  /domain
  /management
  /repository
  /security
  /service
    /dto
    /mapper
  /web/rest