# FazTudoJa

Plataforma web para conexao entre clientes e prestadores de servico.

Projeto academico desenvolvido na FATEC Sao Jose do Rio Preto, no curso de Tecnologia em Informatica para Negocios, como parte do Projeto de Extensao Comunitaria (Curricularizacao).

## Resumo

O FazTudoJa foi desenvolvido como um MPV (Minimo Produto Viavel) para intermediar servicos locais com foco em:

- cadastro e autenticacao de usuarios
- publicacao e gerenciamento de pedidos
- envio e acompanhamento de propostas
- mensageria entre cliente e prestador
- notificacoes e historico
- avaliacoes
- painel administrativo

## Objetivo Academico

Aplicar de forma integrada os conhecimentos de:

- Linguagens de Programacao II
- Administracao de Banco de Dados
- Estatistica
- Gestao da Qualidade e Ambiental

O projeto contempla analise de processo, implementacao full stack, modelagem de dados em SQL Server e apoio a analises de negocio.

## Equipe

- Thadeu Paramo
- Raul Gonsalves

## Tecnologias

### Frontend

- React 18
- Vite 6
- TypeScript
- Tailwind CSS
- Radix UI

### Backend

- Java 21
- Spring Boot 3
- Spring Data JPA
- Maven

### Banco de dados

- SQL Server (principal)
- H2 e PostgreSQL configurados como dependencias de runtime

## Arquitetura

- frontend em app SPA (pasta `trabalho`)
- backend em API REST (pasta `back_fatec`)
- persistencia relacional com JPA/Hibernate
- scripts e documentacao de banco na pasta `back_fatec/docs`

## Estrutura do Repositorio

```text
Projeto_Fatec-main/
  back_fatec/          # API Spring Boot
  trabalho/            # Frontend React + Vite
  docs/                # DER/MER e material academico
  scripts/             # Scripts utilitarios
  start.bat            # Inicializacao automatica (Windows)
  run_dev.bat          # Atalho para start.bat
```

## Requisitos

- Java 21+
- Maven Wrapper (ja incluido no projeto)
- Node.js LTS + npm
- SQL Server em execucao

## Como executar (Windows)

### Opcao 1 - Inicializacao automatica

Na raiz do projeto:

```bat
start.bat
```

ou

```bat
run_dev.bat
```

Esse fluxo inicia:

- backend Spring Boot
- frontend Vite
- tentativa de abrir o SSMS (se instalado)

### Opcao 2 - Inicializacao manual

Backend:

```bat
cd back_fatec
mvnw.cmd spring-boot:run
```

Frontend:

```bat
cd trabalho
npm install
npm run dev
```

## URLs padrao

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`

## Configuracao de banco

Arquivo principal:

- `back_fatec/src/main/resources/application.properties`

Configuracao atual utiliza SQL Server local com:

- porta 1433
- banco `Faztudoja`
- `spring.jpa.hibernate.ddl-auto=update`

## Seeds e scripts SQL

Scripts importantes:

- `back_fatec/docs/seed_sqlserver.sql` (dados iniciais)
- `back_fatec/docs/seed_funcao_categoria.sql` (dados e funcao por categoria)
- `back_fatec/docs/objetos_avancados_fatec.sql` (objetos avancados)
- `back_fatec/docs/limpar_banco.sql` (limpeza de dados para ambiente de dev)

## Principais funcionalidades

- autenticacao e cadastro de usuario
- selecao de perfil (cliente/prestador)
- gerenciamento de pedidos e propostas
- chat entre participantes
- notificacoes
- avaliacao de usuarios
- painel administrativo com controles de status, ativacao e exclusao

## Estado do projeto

- MPV funcional
- fluxos principais implementados
- ajustes continuos de UX, validacoes e padronizacao de testes

## Evidencias academicas

Material de apoio no repositorio:

- relatorio completo de extensao
- relatorio em formato ABNT
- DER/MER e documentacao HTML
- scripts de banco e organizacao de entregas

## Sugestao para apresentacao

Para demo em sala:

1. iniciar com `start.bat`
2. abrir tela de acesso
3. mostrar fluxo cliente -> pedido
4. mostrar fluxo prestador -> proposta
5. mostrar painel admin (gestao de usuarios/pedidos)
6. finalizar com scripts e modelagem de banco

## Licenca

Uso academico.
