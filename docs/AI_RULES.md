\# AI\_RULES.md



\## Objetivo

Este projeto utiliza JHipster com a stack:

\- Spring Boot

\- PostgreSQL

\- Vue

\- Maven

\- Liquibase

\- MapStruct



A IA deve atuar como assistente de desenvolvimento, sempre respeitando a arquitetura, os padrões e as convenções definidas neste projeto.



\---



\## Princípios gerais



1\. Não inventar arquitetura paralela.

2\. Não substituir bibliotecas principais do projeto sem justificativa clara.

3\. Sempre reaproveitar o padrão gerado pelo JHipster.

4\. Toda alteração deve ser mínima, objetiva e compatível com o projeto existente.

5\. Sempre priorizar clareza, legibilidade, manutenção e padronização.

6\. Toda regra de negócio deve ficar centralizada na camada de service.

7\. Toda entrada externa deve ser validada.

8\. Sempre considerar segurança, auditoria e rastreabilidade.

9\. Sempre que possível, seguir o padrão já usado no projeto antes de criar algo novo.

10\. Não alterar nomes, pacotes, convenções ou estrutura base sem necessidade expressa.



\---



\## Estrutura esperada do projeto



\### Backend

\- domain: entidades JPA

\- repository: acesso a dados

\- service: regras de negócio

\- service/dto: objetos de transferência

\- service/mapper: conversores MapStruct

\- web/rest: controllers REST

\- security: autenticação e autorização

\- config: configurações

\- management: monitoramento e métricas



\### Frontend

\- entities: telas e serviços das entidades

\- shared: componentes compartilhados

\- router: rotas

\- locales: internacionalização

\- app/config: configurações do frontend



\---



\## Regras para backend



1\. Controllers devem ser enxutos.

2\. Controllers não devem conter regra de negócio.

3\. Toda lógica de negócio deve ficar em service.

4\. Toda persistência deve passar por repository.

5\. DTO deve ser usado para entrada e saída externa sempre que fizer sentido.

6\. Mapper deve ser usado para converter entidade e DTO.

7\. Não expor diretamente entidades internas sem necessidade.

8\. Validar campos obrigatórios e formatos com Bean Validation.

9\. Exceções devem ser claras e padronizadas.

10\. Sempre que houver listagem, avaliar paginação e filtros.

11\. Sempre que houver busca por campos não triviais, manter padrão REST e de service já existente.

12\. Métodos devem ter nomes claros e condizentes com a responsabilidade.



\---



\## Regras para frontend



1\. Seguir o padrão Vue já existente no projeto.

2\. Não criar uma arquitetura de frontend paralela.

3\. Reaproveitar componentes e serviços gerados pelo JHipster.

4\. Toda tela deve tratar:

&#x20;  - loading

&#x20;  - sucesso

&#x20;  - erro

&#x20;  - estado vazio

5\. Formularios devem apresentar mensagens de validação amigáveis.

6\. Listagens devem considerar paginação, filtros e ordenação quando aplicável.

7\. Não duplicar lógica de consumo de API.

8\. Manter padrão visual e estrutural do projeto.

9\. Sempre preferir consistência com o restante do sistema a “inovação visual”.



\---



\## Regras para banco de dados



1\. Toda alteração estrutural deve ser compatível com Liquibase.

2\. Não fazer mudanças manuais sem registrar migração.

3\. Avaliar constraints:

&#x20;  - not null

&#x20;  - unique

&#x20;  - foreign key

4\. Avaliar necessidade de índice para consultas frequentes.

5\. Manter nomes coerentes e padronizados.

6\. Preservar integridade referencial.



\---



\## Regras para segurança



1\. Toda operação deve respeitar autenticação e autorização do projeto.

2\. Não expor dados sensíveis desnecessariamente.

3\. Validar perfis de acesso em endpoints e operações críticas.

4\. Não confiar em validação apenas no frontend.

5\. Toda validação crítica deve existir também no backend.



\---



\## Regras para testes



1\. Toda regra de negócio relevante deve ter teste.

2\. Criar testes unitários para services.

3\. Criar testes de integração para endpoints importantes.

4\. Cobrir:

&#x20;  - fluxo de sucesso

&#x20;  - falha de validação

&#x20;  - entidade não encontrada

&#x20;  - regra de negócio violada

&#x20;  - casos de borda

5\. Não gerar testes superficiais apenas para “cumprir tabela”.



\---



\## Regras de documentação



1\. Toda funcionalidade relevante deve ser documentada.

2\. Toda API nova deve ter descrição de objetivo, entrada e saída.

3\. Toda decisão importante deve ser registrada no README ou em docs específicas.

4\. Quando criar nova regra de negócio, explicar:

&#x20;  - o que faz

&#x20;  - onde está implementada

&#x20;  - impacto no sistema



\---



\## O que a IA deve fazer



A IA pode ajudar com:

\- geração de JDL

\- modelagem de entidades

\- criação de DTOs

\- criação de mappers

\- implementação de services

\- criação de endpoints

\- criação de filtros

\- criação de testes

\- revisão de código

\- refatoração sem mudança de comportamento

\- documentação técnica

\- análise de stack trace e troubleshooting



\---



\## O que a IA não deve fazer sem revisão rigorosa



\- alterar fluxo de autenticação

\- alterar segurança base do projeto

\- modificar arquitetura principal

\- criar dependências novas sem justificativa

\- criar lógica crítica financeira/jurídica sem revisão

\- alterar migrações existentes sem avaliar impacto

\- fazer refatoração ampla sem delimitar escopo



\---



\## Estilo de resposta esperado da IA



Ao responder sobre implementação, a IA deve:

1\. explicar brevemente a solução

2\. informar os arquivos impactados

3\. mostrar apenas o necessário

4\. manter compatibilidade com o padrão JHipster

5\. evitar código excessivo quando uma alteração pequena resolver



\---



\## Prompt-base para usar com IA



Use este contexto em todas as tarefas:



"Você está trabalhando em um projeto JHipster com Spring Boot, PostgreSQL e Vue. Respeite a arquitetura existente. Não invente padrões paralelos. Toda regra de negócio deve ficar no service, toda persistência no repository, e a comunicação externa deve seguir o padrão REST e DTO do projeto. Sempre mantenha compatibilidade com o padrão do JHipster."



\---

