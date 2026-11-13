# Relatório de testes — Sistema de Ordem de Serviço

**Data:** 12/11/2026  
**Projeto:** API REST de Ordem de Serviço  
**Banco:** PostgreSQL (`ordemservicodb`)

## Ambiente e execução

- Java 21.
- Spring Boot 4.1.2 com Maven Wrapper.
- Aplicação iniciada pelo comando `mvnw.cmd spring-boot:run`.
- Requisições manuais enviadas pelo PowerShell para `http://localhost:8080`.
- Compilação executada com `mvnw.cmd -DskipTests compile`.
- Os testes abaixo foram manuais; não foram executados testes automatizados JUnit.

## Resultados

| Verificação | Requisição/ação | Resultado observado |
|---|---|---|
| Cadastrar cliente | `POST /clientes` | `201 Created`; cliente cadastrado com ID 1. |
| CPF duplicado | Repetir `POST /clientes` com o mesmo CPF | `400 Bad Request`, conforme Regra 1. |
| Cadastrar técnico | `POST /tecnicos` | `201 Created`; técnico cadastrado com ID 1. |
| Abrir OS para cliente existente | `POST /ordens` com cliente ID 1 | `201 Created`; OS criada como `ABERTA`, com data de abertura e sem técnico. |
| Abrir OS para cliente inexistente | `POST /ordens` com cliente ID 99 | `404 Not Found`, conforme Regra 2. |
| Concluir sem técnico | `PUT /ordens/1/concluir?valor=250.00` antes da atribuição | `400 Bad Request`, conforme Regra 6. |
| Atribuir técnico | `PUT /ordens/1/tecnico/1` | Sucesso; OS passou para `EM_ANDAMENTO`, conforme Regra 5. |
| Concluir OS | `PUT /ordens/1/concluir?valor=250.00` | Sucesso; OS passou para `CONCLUIDA`, com valor e data de fechamento, conforme Regra 7. |
| Alterar OS concluída | `PUT /ordens/1/cancelar` | `400 Bad Request`; a OS finalizada não foi alterada, conforme Regra 4. |
| Cancelar outra OS | Criar OS 2 e executar `PUT /ordens/2/cancelar` | Sucesso; OS passou para `CANCELADA` com data de fechamento, conforme Regra 8. |
| Filtrar por prioridade | `GET /ordens/prioridade/BAIXA` | Sucesso; lista retornada com a OS correspondente. |
| Excluir cliente com OS | `DELETE /clientes/1` | `400 Bad Request`; cliente preservado, conforme exercício da Regra 9. |
| Concluir com valor zero | `PUT /ordens/3/concluir?valor=0.00` | `400 Bad Request`, conforme exercício da Regra 10. |
| Concluir com valor positivo | `PUT /ordens/3/concluir?valor=150.00` | Sucesso; OS concluída. |
| Listar cadastros e ordens | `GET /clientes`, `GET /tecnicos`, `GET /ordens` | Sucesso. |
| Buscar registros por ID | `GET /clientes/1`, `GET /tecnicos/1`, `GET /ordens/1` | Sucesso. |
| Filtrar OS | `GET /ordens?status=CONCLUIDA` e `GET /ordens/cliente/1` | Sucesso. |
| Atualizar cliente | `PUT /clientes/1` | `200 OK`; nome, telefone e e-mail atualizados; CPF preservado. |
| Atualizar técnico | `PUT /tecnicos/1` | `200 OK`. |
| Excluir cliente temporário sem OS | Criar cliente temporário e executar `DELETE /clientes/2` | `204 No Content`. |
| Excluir técnico temporário sem OS | Criar técnico temporário e executar `DELETE /tecnicos/2` | `204 No Content`. |

## Observação sobre acentuação

Uma primeira requisição com texto acentuado retornou erro de UTF-8. As requisições seguintes foram enviadas convertendo o JSON explicitamente para UTF-8 e retornaram sucesso. Em algumas respostas, o terminal mostrou acentos com codificação visual incorreta; recomenda-se conferir os nomes e descrições diretamente no pgAdmin antes da entrega.

## Conclusão

Os fluxos manuais de cadastro, consulta, atualização, exclusão e regras de negócio foram exercitados. Os retornos observados corresponderam aos resultados esperados para cada cenário.
