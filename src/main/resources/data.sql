-- 1. CATEGORIA
INSERT INTO categoria (nome) VALUES ('Tecnologia e Informática');
INSERT INTO categoria (nome) VALUES ('Reformas e Reparos');
INSERT INTO categoria (nome) VALUES ('Aulas Particulares');

-- 2. USUARIO (IDs: 1=João, 2=Maria, 3=Carlos)
INSERT INTO usuario (nome, email, tipo, bio, foto, cidade) VALUES 
('João da Silva', 'joao@email.com', 'CLIENTE', 'Busco profissionais qualificados.', 'foto1.jpg', 'São Paulo'),
('Maria Oliveira', 'maria@email.com', 'PRESTADOR', 'Especialista em desenvolvimento Java e suporte.', 'foto2.jpg', 'Rio de Janeiro'),
('Carlos Souza', 'carlos@email.com', 'AMBOS', 'Faço reparos elétricos e contrato aulas.', 'foto3.jpg', 'Curitiba');

-- 3. ENDERECO (Coluna: ID_USUARIO)
INSERT INTO endereco (id_usuario, rua, numero, cidade, bairro, estado, cep) VALUES 
(1, 'Av. Paulista', '1000', 'São Paulo', 'Bela Vista', 'SP', '01310-100'),
(2, 'Rua das Laranjeiras', '50', 'Rio de Janeiro', 'Laranjeiras', 'RJ', '22240-000'),
(3, 'Rua XV de Novembro', '200', 'Curitiba', 'Centro', 'PR', '80020-310');

-- 4. CLIENTE (Tabela de Tipos de Serviço - Coluna FK: ID_CATEGORIA)
INSERT INTO cliente (titulo, descricao, id_categoria) VALUES 
('Formatação de Computador', 'Serviço de formatação e backup', 1),
('Instalação Elétrica', 'Troca de fiação e tomadas', 2),
('Aula de Inglês', 'Conversação e gramática', 3);

-- 5. SERVICO_OFERECIDO (Colunas: ID_USUARIO, ID_SERVICO -> aponta para tabela Cliente)
INSERT INTO servico_oferecido (id_usuario, id_servico, preco_medio, descricao) VALUES 
(2, 1, 150.00, 'Formatação completa com instalação de drivers e office.'),
(3, 2, 200.00, 'Instalação de chuveiros e revisão de quadro de força.');

-- 6. OFERTA (Colunas: ID_USUARIO, ID_SERVICO -> aponta para tabela Cliente)
INSERT INTO oferta (id_usuario, id_servico, titulo, descricao, localizacao) VALUES 
(2, 1, 'Promoção Formatação', 'Desconto de 10% para pagamentos via PIX', 'Rio de Janeiro - Centro');

-- 7. PEDIDO (Colunas: ID_USUARIO, ID_ENDERECO, ID_SERVICO -> aponta para ServicoOferecido)
INSERT INTO pedido (id_usuario, id_servico, titulo, descricao, localizacao, status, id_endereco) VALUES 
(1, 1, 'Preciso formatar notebook', 'Meu notebook está muito lento, preciso urgente.', 'São Paulo', 'ABERTO', 1);

-- 8. PROPOSTA (Colunas: ID_PEDIDO, ID_PRESTADOR)
INSERT INTO proposta (id_pedido, id_prestador, preco_proposto, status, mensagem) VALUES 
(1, 2, 140.00, 'AGUARDANDO', 'Olá João, consigo fazer por 140 se eu puder buscar o equipamento amanhã.');

-- 9. PARTICIPANTE (Colunas: ID_USER_CLIENTE, ID_USER_PRESTADOR)
INSERT INTO participante (id_user_cliente, id_user_prestador, aceite_cliente, aceite_prestador, aceite_timestamp) VALUES 
(1, 2, true, true, '2024-01-20 10:00:00');

-- 10. MENSAGEM (Colunas: ID_PARTICIPANTE, ID_REMETENTE)
INSERT INTO mensagem (id_participante, id_remetente, conteudo, tipo, timestamp) VALUES 
(1, 1, 'Olá, qual o prazo de entrega?', 'TEXTO', '2024-01-20 10:05:00'),
(1, 2, 'Entrego em 24 horas.', 'TEXTO', '2024-01-20 10:06:00');

-- 11. PAGAMENTO (Colunas: ID_USUARIO, ID_PEDIDO)
INSERT INTO pagamento (id_usuario, id_pedido, valor, status, metodo) VALUES 
(1, 1, 140.00, 'PENDENTE', 'PIX');

-- 12. AVALIACAO (Colunas: ID_AVALIADOR, ID_AVALIADO)
INSERT INTO avaliacao (id_avaliador, id_avaliado, nota, comentario, data) VALUES 
(1, 2, 5, 'Excelente profissional, muito rápida!', '2024-01-22 15:00:00');

-- 13. NOTIFICACAO (Coluna: ID_USUARIO)
INSERT INTO notificacao (id_usuario, tipo, mensagem, data, lida) VALUES 
(2, 'SISTEMA', 'Você recebeu uma nova proposta aceita.', '2024-01-20 11:00:00', false);

-- 14. AGENDA (Coluna Estranha: ID_AGENDA parece ser a FK para Usuario baseada no seu log)
INSERT INTO agenda (id_agenda, data, hora_inicio, hora_fim, disponivel) VALUES 
(2, '2024-01-25', '08:00:00', '18:00:00', true);

-- 15. CONTRATO (Colunas: ID_PEDIDO, ID_CLIENTE, ID_PRESTADOR, ID_PARTICIPANTE)
INSERT INTO contrato (id_pedido, id_cliente, id_prestador, id_participante, termos, status) VALUES 
(1, 1, 2, 1, 'Contrato de prestação de serviço de informática...', 'ATIVO');