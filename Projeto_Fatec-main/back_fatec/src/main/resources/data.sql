-- 1. CATEGORIAS
INSERT INTO categorias (nome) VALUES ('Tecnologia e Informática');
INSERT INTO categorias (nome) VALUES ('Reformas e Reparos');
INSERT INTO categorias (nome) VALUES ('Aulas Particulares');

-- 2. SERVICO_CATALOGO (Tabela de Tipos de Serviço - Coluna FK: ID_CATEGORIA)
INSERT INTO servico_catalogo (titulo, descricao, id_categoria) VALUES 
('Formatação de Computador', 'Serviço de formatação e backup', 1),
('Instalação Elétrica', 'Troca de fiação e tomadas', 2),
('Aula de Inglês', 'Conversação e gramática', 3);

-- 16. VIEWS DE CONSULTA NO H2
CREATE VIEW vw_clientes_usuario AS
SELECT id, nome, email, senha, ativo, tipo, cpf, telefone, endereco, estado, cep, bio, foto, cidade
FROM usuarios
WHERE UPPER(tipo) = 'CLIENTE';

CREATE VIEW vw_prestadores_usuario AS
SELECT id, nome, email, senha, ativo, tipo, cpf, telefone, endereco, estado, cep, bio, foto, cidade
FROM usuarios
WHERE UPPER(tipo) = 'PRESTADOR';

CREATE VIEW endereco AS
SELECT * FROM enderecos;

CREATE VIEW servicos_catalogo AS
SELECT s.id,
	   s.titulo,
	   s.descricao,
	   c.nome AS categoria
FROM servico_catalogo s
LEFT JOIN categorias c ON c.id = s.id_categoria;

CREATE VIEW pedidos_detalhados AS
SELECT p.id,
	   p.titulo,
	   p.descricao,
	   p.localizacao,
	   p.status,
	   p.contato_nome,
	   p.contato_email,
	   p.contato_telefone,
	   u.id AS cliente_id,
	   u.nome AS cliente_nome,
	   u.email AS cliente_email,
	   s.id AS servico_id,
	   s.titulo AS servico_titulo,
	   c.nome AS categoria,
	   e.id AS endereco_id,
	   e.rua,
	   e.numero,
	   e.bairro,
	   e.cidade,
	   e.estado,
	   e.cep
FROM pedidos p
JOIN usuarios u ON u.id = p.id_usuario
LEFT JOIN servico_catalogo s ON s.id = p.id_servico
LEFT JOIN categorias c ON c.id = s.id_categoria
LEFT JOIN enderecos e ON e.id = p.id_endereco;

CREATE VIEW propostas_detalhadas AS
SELECT pr.id,
	   pr.preco_proposto,
	   pr.status,
	   pr.mensagem,
	   p.id AS pedido_id,
	   p.titulo AS pedido_titulo,
	   p.status AS pedido_status,
	   cli.id AS cliente_id,
	   cli.nome AS cliente_nome,
	   prest.id AS prestador_id,
	   prest.nome AS prestador_nome,
	   prest.email AS prestador_email
FROM propostas pr
JOIN pedidos p ON p.id = pr.id_pedido
JOIN usuarios cli ON cli.id = p.id_usuario
JOIN usuarios prest ON prest.id = pr.id_prestador;

CREATE VIEW avaliacoes_detalhadas AS
SELECT a.id,
	   a.nota,
	   a.comentario,
	   a.data,
	   av.id AS avaliador_id,
	   av.nome AS avaliador_nome,
	   av.tipo AS avaliador_tipo,
	   ad.id AS avaliado_id,
	   ad.nome AS avaliado_nome,
	   ad.tipo AS avaliado_tipo
FROM avaliacoes a
JOIN usuarios av ON av.id = a.id_avaliador
JOIN usuarios ad ON ad.id = a.id_avaliado;