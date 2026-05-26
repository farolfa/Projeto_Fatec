# DER e MER — FazTudoJA

---

## DER — Diagrama Entidade-Relacionamento

> Notação: entidades em **MAIÚSCULO**, atributo sublinhado = chave primária,
> atributo com * = chave estrangeira, cardinalidades no padrão (min, max).

```
╔══════════════════╗         (1,1)          ╔══════════════════╗
║    CATEGORIA     ║ ─────────────────────── ║ SERVICO_CATALOGO ║
╠══════════════════╣  classifica           ╠══════════════════╣
║ <id>             ║                        ║ <id>             ║
║  nome            ║                        ║  titulo          ║
╚══════════════════╝                        ║  descricao       ║
                                            ║ *id_categoria    ║
                                            ╚══════════════════╝
                                                    │ (1,N)
                                                    │ é base para
                                            ╔══════════════════╗
                                            ║SERVICO_OFERECIDO ║
                                            ╠══════════════════╣
                                            ║ <id>             ║
                                            ║ *id_usuario      ║
                                            ║ *id_servico      ║
                                            ║  preco_medio     ║
                                            ║  descricao       ║
                                            ╚══════════════════╝
                                                    │ (N,1)
                                                    │ ofertado por
╔══════════════════╗                        ╔══════════════════╗
║    ENDERECO      ║  (N,1)  pertence a     ║     USUARIO      ║
╠══════════════════╣ ──────────────────────>╠══════════════════╣
║ <id>             ║                        ║ <id>             ║
║ *id_usuario      ║                        ║  nome            ║
║  rua             ║         (N,1)          ║  email           ║
║  numero          ║ abre   ┌──────────────>║  senha           ║
║  bairro          ║        │               ║  cpf             ║
║  cidade          ║        │               ║  telefone        ║
║  estado          ║        │               ║  tipo            ║
║  cep             ║        │               ║  status          ║
╚══════════════════╝        │               ║  ativo           ║
                            │               ║  endereco        ║
╔══════════════════╗        │               ║  cidade          ║
║  TICKET_SUPORTE  ║        │               ║  estado          ║
╠══════════════════╣        │               ║  cep             ║
║ <id>             ║        │               ║  bio             ║
║ *id_usuario ─────╫────────┘               ║  foto            ║
║  assunto         ║                        ╚══════╤═══════════╝
║  mensagem        ║                               │
║  categoria       ║      ┌────────────────────────┼──────────────────────────────┐
║  status          ║      │                        │                              │
║  criado_em       ║      │ herança (1,1)           │ herança (1,1)                │ herança (1,1)
╚══════════════════╝      ▼                        ▼                              ▼
        │ (1,N)   ╔══════════════════╗  ╔══════════════════╗             (admin — sem tabela própria)
        │         ║    PRESTADOR     ║  ║     CLIENTE      ║
╔══════════════╗  ╠══════════════════╣  ╠══════════════════╣
║ RESP_TICKET  ║  ║ <id_usuario> PK/FK║  ║ <id_usuario> PK/FK║
╠══════════════╣  ║  nome_profissional║  ║  apelido         ║
║ <id>         ║  ║  especialidade   ║  ║  observacao      ║
║ *id_ticket   ║  ║  descricao       ║  ╚══════════════════╝
║  respondente ║  ╚══════════════════╝
║  resposta    ║
║  respondido_em║
╚══════════════╝
```

---

### Relacionamentos principais (formato acadêmico)

```
USUARIO ────────────────────── PEDIDO
         (1,N) cria            (N,1)
          um usuário (cliente) cria N pedidos
          um pedido pertence a 1 usuário

PEDIDO ─────────────────────── PROPOSTA
        (1,N) recebe           (N,1)
         um pedido recebe N propostas
         uma proposta pertence a 1 pedido

USUARIO ────────────────────── PROPOSTA
         (1,N) envia           (N,1)
          um prestador envia N propostas
          uma proposta é enviada por 1 prestador

USUARIO ────────────────────── AVALIACAO (como avaliador)
         (1,N) avalia          (N,1)

USUARIO ────────────────────── AVALIACAO (como avaliado)
         (1,N) é avaliado      (N,1)

USUARIO ────────────────────── FAVORITO
         (1,N) favorita        (N,1)

USUARIO ────────────────────── PARTICIPANTE (como cliente)
         (1,N)                 (N,1)

USUARIO ────────────────────── PARTICIPANTE (como prestador)
         (1,N)                 (N,1)

PARTICIPANTE ─────────────── MENSAGEM
              (1,N) contém    (N,1)

USUARIO ────────────────────── MENSAGEM (como remetente)
         (1,N) envia           (N,1)

USUARIO ────────────────────── NOTIFICACAO
         (1,N) recebe          (N,1)

CATEGORIA ─────────────────── SERVICO_CATALOGO
           (1,N) classifica    (N,1)

SERVICO_CATALOGO ─────────── SERVICO_OFERECIDO
                  (1,N)        (N,1)

USUARIO ─────────────────── SERVICO_OFERECIDO
         (1,N) oferece        (N,1)

TICKET_SUPORTE ────────────── RESPOSTA_TICKET
                (1,N) possui   (N,1)
```

---

### DER Completo — Notação Crow's Foot (Mermaid)

> Abrir no VS Code com extensão Mermaid ou em https://mermaid.live

```mermaid
erDiagram

    USUARIO {
        bigint id PK
        varchar nome
        varchar email
        varchar senha
        varchar cpf
        varchar telefone
        varchar tipo
        int status
        bit ativo
        varchar endereco
        varchar cidade
        varchar estado
        varchar cep
        varchar bio
        varchar foto
    }

    PRESTADOR {
        bigint id_usuario PK
        varchar nome_profissional
        varchar especialidade
        varchar descricao
    }

    CLIENTE {
        bigint id_usuario PK
        varchar apelido
        varchar observacao
    }

    CATEGORIA {
        bigint id PK
        varchar nome
    }

    SERVICO_CATALOGO {
        bigint id PK
        varchar titulo
        varchar descricao
        bigint id_categoria FK
    }

    SERVICO_OFERECIDO {
        bigint id PK
        bigint id_usuario FK
        bigint id_servico FK
        decimal preco_medio
        varchar descricao
    }

    ENDERECO {
        bigint id PK
        bigint id_usuario FK
        varchar rua
        varchar numero
        varchar bairro
        varchar cidade
        varchar estado
        varchar cep
    }

    PEDIDO {
        bigint id PK
        bigint id_usuario FK
        bigint id_servico FK
        bigint id_endereco FK
        varchar titulo
        varchar descricao
        varchar localizacao
        varchar status
        varchar contato_nome
        varchar contato_email
        varchar contato_telefone
        bit cliente_confirmou_conclusao
        bit prestador_confirmou_conclusao
        datetime data_criacao
    }

    PROPOSTA {
        int id PK
        bigint id_pedido FK
        bigint id_prestador FK
        decimal preco_proposto
        varchar status
        varchar mensagem
        varchar prazo_entrega
        datetime data_criacao
    }

    AVALIACAO {
        bigint id PK
        bigint id_avaliador FK
        bigint id_avaliado FK
        bigint nota
        varchar comentario
        datetime data
    }

    FAVORITO {
        bigint id PK
        bigint id_usuario FK
        bigint prestador_id FK
        varchar prestador_nome
        varchar prestador_foto
        datetime saved_at
    }

    PARTICIPANTE {
        bigint id PK
        bigint id_user_cliente FK
        bigint id_user_prestador FK
        bit aceite_cliente
        bit aceite_prestador
        datetime aceite_timestamp
        bigint pedido_referencia FK
        varchar titulo_servico
    }

    MENSAGEM {
        bigint id PK
        bigint id_participante FK
        bigint id_remetente FK
        varchar conteudo
        varchar tipo
        datetime timestamp
        bit lida
    }

    NOTIFICACAO {
        bigint id PK
        bigint id_usuario FK
        varchar tipo
        varchar mensagem
        datetime data
        bit lida
    }

    NOTIFICACAO_LIDA_EXTERNA {
        bigint id PK
        bigint usuario_id FK
        varchar external_id
    }

    TICKET_SUPORTE {
        bigint id PK
        bigint id_usuario FK
        varchar assunto
        varchar mensagem
        varchar categoria
        varchar status
        datetime criado_em
    }

    RESPOSTA_TICKET {
        bigint id PK
        bigint id_ticket FK
        varchar respondente
        varchar resposta
        datetime respondido_em
    }

    %% Relacionamentos detalhados
    USUARIO ||--o| PRESTADOR : "herança (é-um)"
    USUARIO ||--o| CLIENTE   : "herança (é-um)"
    CATEGORIA ||--o{ SERVICO_CATALOGO : "classifica (1,N)"
    SERVICO_CATALOGO ||--o{ SERVICO_OFERECIDO : "é ofertado (1,N)"
    USUARIO ||--o{ SERVICO_OFERECIDO : "oferece (1,N)"
    USUARIO ||--o{ ENDERECO : "possui (1,N)"
    USUARIO ||--o{ PEDIDO : "cria (1,N)"
    SERVICO_CATALOGO ||--o{ PEDIDO : "referenciado em (1,N)"
    ENDERECO ||--o{ PEDIDO : "localiza (1,N)"
    PEDIDO ||--o{ PROPOSTA : "recebe (1,N)"
    USUARIO ||--o{ PROPOSTA : "envia (1,N)"
    USUARIO ||--o{ AVALIACAO : "avalia (avaliador 1,N)"
    USUARIO ||--o{ AVALIACAO : "recebe (avaliado 1,N)"
    USUARIO ||--o{ FAVORITO : "favorita (1,N)"
    USUARIO ||--o{ PARTICIPANTE : "participa como cliente (1,N)"
    USUARIO ||--o{ PARTICIPANTE : "participa como prestador (1,N)"
    PARTICIPANTE ||--o{ MENSAGEM : "contém (1,N)"
    USUARIO ||--o{ MENSAGEM : "envia (1,N)"
    USUARIO ||--o{ NOTIFICACAO : "recebe (1,N)"
    USUARIO ||--o{ NOTIFICACAO_LIDA_EXTERNA : "marca notificacao externa lida (1,N)"
    USUARIO ||--o{ TICKET_SUPORTE : "abre (1,N)"
    TICKET_SUPORTE ||--o{ RESPOSTA_TICKET : "possui (1,N)"
```

---

## MER — Modelo Entidade-Relacionamento (Esquema Relacional)

> Notação: **PK** = Chave Primária | **FK** = Chave Estrangeira | **UK** = Unique | **NN** = Not Null

---

### USUARIO (_id_, nome, email, senha, cpf, telefone, tipo, status, ativo, endereco, cidade, estado, cep, bio, foto)
- **PK:** id
- **UK:** email, cpf
- tipo ∈ {CLIENTE, PRESTADOR, ADMIN, ADMIN_PRINCIPAL}

---

### PRESTADOR (_id\_usuario_, nome\_profissional, especialidade, descricao)
- **PK:** id\_usuario
- **FK:** id\_usuario → USUARIO(id)  *(herança JOINED)*
- nome\_profissional NN, especialidade NN

---

### CLIENTE (_id\_usuario_, apelido, observacao)
- **PK:** id\_usuario
- **FK:** id\_usuario → USUARIO(id)  *(herança JOINED)*

---

### CATEGORIA (_id_, nome)
- **PK:** id

---

### SERVICO\_CATALOGO (_id_, titulo, descricao, id\_categoria)
- **PK:** id
- **FK:** id\_categoria → CATEGORIA(id)

---

### SERVICO\_OFERECIDO (_id_, id\_usuario, id\_servico, preco\_medio, descricao)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id)
- **FK:** id\_servico → SERVICO\_CATALOGO(id)

---

### ENDERECO (_id_, id\_usuario, rua, numero, bairro, cidade, estado, cep)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id)

---

### PEDIDO (_id_, id\_usuario, id\_servico, id\_endereco, titulo, descricao, localizacao, status, contato\_nome, contato\_email, contato\_telefone, cliente\_confirmou\_conclusao, prestador\_confirmou\_conclusao, data\_criacao)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id) NN
- **FK:** id\_servico → SERVICO\_CATALOGO(id)
- **FK:** id\_endereco → ENDERECO(id)
- status ∈ {ABERTO, EM_ANDAMENTO, CONCLUIDO, CANCELADO, AGUARDANDO_CONFIRMACAO}

---

### PROPOSTA (_id_, id\_pedido, id\_prestador, preco\_proposto, status, mensagem, prazo\_entrega, data\_criacao)
- **PK:** id
- **FK:** id\_pedido → PEDIDO(id) NN
- **FK:** id\_prestador → USUARIO(id) NN
- **UK:** (id\_pedido, id\_prestador)
- status ∈ {PENDENTE, ACEITA, RECUSADA, CANCELADA}

---

### AVALIACAO (_id_, id\_avaliador, id\_avaliado, nota, comentario, data)
- **PK:** id
- **FK:** id\_avaliador → USUARIO(id) NN
- **FK:** id\_avaliado → USUARIO(id) NN
- **UK:** (id\_avaliador, id\_avaliado)
- nota NN (1 a 5)

---

### FAVORITO (_id_, id\_usuario, prestador\_id, prestador\_nome, prestador\_foto, saved\_at)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id) NN
- **UK:** (id\_usuario, prestador\_id)

---

### PARTICIPANTE (_id_, id\_user\_cliente, id\_user\_prestador, aceite\_cliente, aceite\_prestador, aceite\_timestamp, pedido\_referencia, titulo\_servico)
- **PK:** id
- **FK:** id\_user\_cliente → USUARIO(id)
- **FK:** id\_user\_prestador → USUARIO(id)

---

### MENSAGEM (_id_, id\_participante, id\_remetente, conteudo, tipo, timestamp, lida)
- **PK:** id
- **FK:** id\_participante → PARTICIPANTE(id)
- **FK:** id\_remetente → USUARIO(id)

---

### NOTIFICACAO (_id_, id\_usuario, tipo, mensagem, data, lida)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id)

---

### NOTIFICACAO\_LIDA\_EXTERNA (_id_, usuario\_id, external\_id)
- **PK:** id
- **FK:** usuario\_id → USUARIO(id)
- **UK:** (usuario\_id, external\_id)

---

### TICKET\_SUPORTE (_id_, id\_usuario, assunto, mensagem, categoria, status, criado\_em)
- **PK:** id
- **FK:** id\_usuario → USUARIO(id) NN
- status ∈ {aberto, em\_andamento, fechado}

---

### RESPOSTA\_TICKET (_id_, id\_ticket, respondente, resposta, respondido\_em)
- **PK:** id
- **FK:** id\_ticket → TICKET\_SUPORTE(id) NN
