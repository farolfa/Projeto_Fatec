-- =========================================================
-- SEED DATA para SQL Server (rodar APÓS o Spring criar as tabelas)
-- Servidor: LAPTOP-QRV0HV9A\SQLEXPRESS  Banco: Faztudoja
-- =========================================================
USE Faztudoja;
GO

-- 1. CATEGORIAS
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Tecnologia e Informática')
    INSERT INTO categorias (nome) VALUES ('Tecnologia e Informática');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Reformas e Reparos')
    INSERT INTO categorias (nome) VALUES ('Reformas e Reparos');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Aulas Particulares')
    INSERT INTO categorias (nome) VALUES ('Aulas Particulares');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Limpeza e Conservação')
    INSERT INTO categorias (nome) VALUES ('Limpeza e Conservação');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Beleza e Estética')
    INSERT INTO categorias (nome) VALUES ('Beleza e Estética');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Transporte e Mudanças')
    INSERT INTO categorias (nome) VALUES ('Transporte e Mudanças');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Saúde e Bem-estar')
    INSERT INTO categorias (nome) VALUES ('Saúde e Bem-estar');
IF NOT EXISTS (SELECT 1 FROM categorias WHERE nome = 'Eventos e Entretenimento')
    INSERT INTO categorias (nome) VALUES ('Eventos e Entretenimento');
GO

-- 2. SERVICO_CATALOGO — Tecnologia e Informática
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Formatação de Computador')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Formatação de Computador', 'Formatação completa com backup e instalação de drivers', id FROM categorias WHERE nome = 'Tecnologia e Informática';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Instalação de Redes Wi-Fi')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Instalação de Redes Wi-Fi', 'Configuração de roteadores e pontos de acesso', id FROM categorias WHERE nome = 'Tecnologia e Informática';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Manutenção de Notebook')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Manutenção de Notebook', 'Limpeza interna, troca de pasta térmica e reparo de hardware', id FROM categorias WHERE nome = 'Tecnologia e Informática';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Suporte Técnico Remoto')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Suporte Técnico Remoto', 'Resolução de problemas via acesso remoto', id FROM categorias WHERE nome = 'Tecnologia e Informática';

-- Reformas e Reparos
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Instalação Elétrica')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Instalação Elétrica', 'Troca de fiação, tomadas e disjuntores', id FROM categorias WHERE nome = 'Reformas e Reparos';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Encanamento e Hidráulica')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Encanamento e Hidráulica', 'Conserto de vazamentos, encanamentos e registros', id FROM categorias WHERE nome = 'Reformas e Reparos';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Pintura Residencial')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Pintura Residencial', 'Pintura interna e externa com acabamento profissional', id FROM categorias WHERE nome = 'Reformas e Reparos';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Montagem de Móveis')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Montagem de Móveis', 'Montagem de móveis de todos os fabricantes', id FROM categorias WHERE nome = 'Reformas e Reparos';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Instalação de Ar Condicionado')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Instalação de Ar Condicionado', 'Instalação e manutenção de ar condicionado split', id FROM categorias WHERE nome = 'Reformas e Reparos';

-- Aulas Particulares
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Aula de Inglês')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Aula de Inglês', 'Conversação, gramática e preparação para exames', id FROM categorias WHERE nome = 'Aulas Particulares';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Aula de Matemática')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Aula de Matemática', 'Reforço escolar e preparação para vestibular', id FROM categorias WHERE nome = 'Aulas Particulares';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Aula de Violão e Guitarra')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Aula de Violão e Guitarra', 'Do iniciante ao avançado', id FROM categorias WHERE nome = 'Aulas Particulares';

-- Limpeza e Conservação
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Limpeza Residencial')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Limpeza Residencial', 'Limpeza completa de casas e apartamentos', id FROM categorias WHERE nome = 'Limpeza e Conservação';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Limpeza Pós-Obra')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Limpeza Pós-Obra', 'Remoção de resíduos e limpeza pesada após reformas', id FROM categorias WHERE nome = 'Limpeza e Conservação';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Limpeza de Estofados')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Limpeza de Estofados', 'Sofás, colchões e cadeiras com extratora', id FROM categorias WHERE nome = 'Limpeza e Conservação';

-- Beleza e Estética
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Corte de Cabelo a Domicílio')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Corte de Cabelo a Domicílio', 'Corte masculino e feminino no conforto da sua casa', id FROM categorias WHERE nome = 'Beleza e Estética';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Manicure e Pedicure')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Manicure e Pedicure', 'Cuidados completos com unhas a domicílio', id FROM categorias WHERE nome = 'Beleza e Estética';

-- Transporte e Mudanças
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Frete e Mudança')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Frete e Mudança', 'Transporte de móveis e objetos com veículo utilitário', id FROM categorias WHERE nome = 'Transporte e Mudanças';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Moto Táxi e Entregas')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Moto Táxi e Entregas', 'Entregas rápidas e moto táxi na cidade', id FROM categorias WHERE nome = 'Transporte e Mudanças';

-- Saúde e Bem-estar
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Personal Trainer')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Personal Trainer', 'Treinos personalizados em casa ou academia', id FROM categorias WHERE nome = 'Saúde e Bem-estar';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Cuidador de Idosos')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Cuidador de Idosos', 'Acompanhamento e cuidados com idosos', id FROM categorias WHERE nome = 'Saúde e Bem-estar';

-- Eventos e Entretenimento
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'Fotografia de Eventos')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'Fotografia de Eventos', 'Cobertura fotográfica de festas, casamentos e formaturas', id FROM categorias WHERE nome = 'Eventos e Entretenimento';
IF NOT EXISTS (SELECT 1 FROM servico_catalogo WHERE titulo = 'DJ para Festas')
    INSERT INTO servico_catalogo (titulo, descricao, id_categoria)
    SELECT 'DJ para Festas', 'Sonorização e animação musical para eventos', id FROM categorias WHERE nome = 'Eventos e Entretenimento';
GO

-- 3. VIEWS (apenas se não existirem)
-- =========================================================
-- 3. VIEWS
-- =========================================================

-- View principal: usuario + telefone + endereco (FK)
IF OBJECT_ID('vw_usuarios_contato', 'V') IS NULL
EXEC ('
CREATE VIEW vw_usuarios_contato AS
SELECT
    u.id,
    u.nome,
    u.email,
    u.tipo,
    u.cpf,
    u.ativo,
    t.numero      AS telefone,
    t.tipo        AS tipo_telefone,
    e.rua         AS endereco,
    e.cidade,
    e.estado,
    e.cep
FROM usuarios u
LEFT JOIN telefones t  ON t.id_usuario  = u.id AND t.principal = 1
LEFT JOIN enderecos e  ON e.id_usuario  = u.id
');
GO

IF OBJECT_ID('vw_clientes_usuario', 'V') IS NULL
EXEC ('
CREATE VIEW vw_clientes_usuario AS
SELECT u.id, u.nome, u.email, u.ativo, u.tipo, u.cpf,
       t.numero AS telefone,
       e.rua AS endereco, e.cidade, e.estado, e.cep,
       u.bio, u.foto
FROM usuarios u
LEFT JOIN telefones t ON t.id_usuario = u.id AND t.principal = 1
LEFT JOIN enderecos e ON e.id_usuario = u.id
WHERE u.tipo = ''CLIENTE''
');
GO

IF OBJECT_ID('vw_prestadores_usuario', 'V') IS NULL
EXEC ('
CREATE VIEW vw_prestadores_usuario AS
SELECT u.id, u.nome, u.email, u.ativo, u.tipo, u.cpf,
       t.numero AS telefone,
       e.rua AS endereco, e.cidade, e.estado, e.cep,
       u.bio, u.foto
FROM usuarios u
LEFT JOIN telefones t ON t.id_usuario = u.id AND t.principal = 1
LEFT JOIN enderecos e ON e.id_usuario = u.id
WHERE u.tipo = ''PRESTADOR''
');
GO

IF OBJECT_ID('servicos_catalogo', 'V') IS NULL
EXEC ('
CREATE VIEW servicos_catalogo AS
SELECT s.id, s.titulo, s.descricao, c.nome AS categoria
FROM servico_catalogo s
LEFT JOIN categorias c ON c.id = s.id_categoria
');
GO

IF OBJECT_ID('pedidos_detalhados', 'V') IS NULL
EXEC ('
CREATE VIEW pedidos_detalhados AS
SELECT p.id, p.titulo, p.descricao, p.localizacao, p.status,
       p.contato_nome, p.contato_email, p.contato_telefone,
       u.id AS cliente_id, u.nome AS cliente_nome, u.email AS cliente_email,
       s.id AS servico_id, s.titulo AS servico_titulo,
       c.nome AS categoria,
       e.id AS endereco_id, e.rua, e.cidade, e.estado, e.cep
FROM pedidos p
JOIN usuarios u ON u.id = p.id_usuario
LEFT JOIN servico_catalogo s ON s.id = p.id_servico
LEFT JOIN categorias c ON c.id = s.id_categoria
LEFT JOIN enderecos e ON e.id = p.id_endereco
');
GO

IF OBJECT_ID('propostas_detalhadas', 'V') IS NULL
EXEC ('
CREATE VIEW propostas_detalhadas AS
SELECT pr.id, pr.preco_proposto, pr.status, pr.mensagem,
       p.id AS pedido_id, p.titulo AS pedido_titulo, p.status AS pedido_status,
       cli.id AS cliente_id, cli.nome AS cliente_nome,
       prest.id AS prestador_id, prest.nome AS prestador_nome, prest.email AS prestador_email
FROM propostas pr
JOIN pedidos p ON p.id = pr.id_pedido
JOIN usuarios cli ON cli.id = p.id_usuario
JOIN usuarios prest ON prest.id = pr.id_prestador
');
GO

IF OBJECT_ID('avaliacoes_detalhadas', 'V') IS NULL
EXEC ('
CREATE VIEW avaliacoes_detalhadas AS
SELECT a.id, a.nota, a.comentario, a.data,
       av.id AS avaliador_id, av.nome AS avaliador_nome, av.tipo AS avaliador_tipo,
       ad.id AS avaliado_id, ad.nome AS avaliado_nome, ad.tipo AS avaliado_tipo
FROM avaliacoes a
JOIN usuarios av ON av.id = a.id_avaliador
JOIN usuarios ad ON ad.id = a.id_avaliado
');
GO

PRINT 'Seed concluído com sucesso!';

-- =========================================================
-- DADOS DE TESTE: Prestadores + Avaliações
-- Rodar somente em ambiente de desenvolvimento
-- =========================================================

-- Prestadores de teste
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'joao.silva@teste.com')
BEGIN
    INSERT INTO usuarios (nome, email, senha, ativo, tipo, cpf, bio)
    VALUES ('João Silva', 'joao.silva@teste.com', 'Teste@123', 1, 'PRESTADOR',
            '11111111111', 'Eletricista com 8 anos de experiência. Atendo residências e comércios.');
    INSERT INTO telefones (id_usuario, numero, tipo, principal)
    SELECT id, '11911111111', 'celular', 1 FROM usuarios WHERE email = 'joao.silva@teste.com';
    INSERT INTO enderecos (id_usuario, rua, cidade, estado, cep)
    SELECT id, 'Rua das Flores', 'São Paulo', 'SP', '01310100' FROM usuarios WHERE email = 'joao.silva@teste.com';
END
GO

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'maria.santos@teste.com')
BEGIN
    INSERT INTO usuarios (nome, email, senha, ativo, tipo, cpf, bio)
    VALUES ('Maria Santos', 'maria.santos@teste.com', 'Teste@123', 1, 'PRESTADOR',
            '22222222222', 'Especialista em limpeza residencial e pós-obra. Mais de 500 clientes atendidos.');
    INSERT INTO telefones (id_usuario, numero, tipo, principal)
    SELECT id, '11922222222', 'celular', 1 FROM usuarios WHERE email = 'maria.santos@teste.com';
    INSERT INTO enderecos (id_usuario, rua, cidade, estado, cep)
    SELECT id, 'Av. Paulista', 'São Paulo', 'SP', '01310200' FROM usuarios WHERE email = 'maria.santos@teste.com';
END
GO

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'carlos.tech@teste.com')
BEGIN
    INSERT INTO usuarios (nome, email, senha, ativo, tipo, cpf, bio)
    VALUES ('Carlos Tech', 'carlos.tech@teste.com', 'Teste@123', 1, 'PRESTADOR',
            '33333333333', 'Técnico em informática. Formatações, redes Wi-Fi e suporte remoto.');
    INSERT INTO telefones (id_usuario, numero, tipo, principal)
    SELECT id, '11933333333', 'celular', 1 FROM usuarios WHERE email = 'carlos.tech@teste.com';
    INSERT INTO enderecos (id_usuario, rua, cidade, estado, cep)
    SELECT id, 'Rua Augusta', 'São Paulo', 'SP', '01305000' FROM usuarios WHERE email = 'carlos.tech@teste.com';
END
GO

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'ana.personal@teste.com')
BEGIN
    INSERT INTO usuarios (nome, email, senha, ativo, tipo, cpf, bio)
    VALUES ('Ana Fitness', 'ana.personal@teste.com', 'Teste@123', 1, 'PRESTADOR',
            '44444444444', 'Personal trainer certificada. Treinos personalizados em casa ou na academia.');
    INSERT INTO telefones (id_usuario, numero, tipo, principal)
    SELECT id, '11944444444', 'celular', 1 FROM usuarios WHERE email = 'ana.personal@teste.com';
    INSERT INTO enderecos (id_usuario, rua, cidade, estado, cep)
    SELECT id, 'Rua Oscar Freire', 'São Paulo', 'SP', '01426001' FROM usuarios WHERE email = 'ana.personal@teste.com';
END
GO

-- Cliente de teste
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'cliente@teste.com')
BEGIN
    INSERT INTO usuarios (nome, email, senha, ativo, tipo, cpf, bio)
    VALUES ('Pedro Cliente', 'cliente@teste.com', 'Teste@123', 1, 'CLIENTE', '55555555555', NULL);
    INSERT INTO telefones (id_usuario, numero, tipo, principal)
    SELECT id, '11955555555', 'celular', 1 FROM usuarios WHERE email = 'cliente@teste.com';
    INSERT INTO enderecos (id_usuario, rua, cidade, estado, cep)
    SELECT id, 'Rua Bela Cintra', 'São Paulo', 'SP', '01415001' FROM usuarios WHERE email = 'cliente@teste.com';
END
GO

-- Avaliações de teste (vincula cliente ao prestador)
-- João Silva: 5 avaliações
IF NOT EXISTS (SELECT 1 FROM avaliacoes WHERE id_avaliador = (SELECT id FROM usuarios WHERE email='cliente@teste.com')
                                          AND id_avaliado  = (SELECT id FROM usuarios WHERE email='joao.silva@teste.com'))
BEGIN
    INSERT INTO avaliacoes (id_avaliador, id_avaliado, nota, comentario, data)
    SELECT c.id, p.id, 5, 'Excelente profissional! Trabalho impecável e pontual.', GETDATE()
    FROM usuarios c, usuarios p
    WHERE c.email = 'cliente@teste.com' AND p.email = 'joao.silva@teste.com';
END
GO

-- Maria Santos: 4 avaliações (vários clientes fictícios via subquery se existirem)
IF NOT EXISTS (SELECT 1 FROM avaliacoes WHERE id_avaliador = (SELECT id FROM usuarios WHERE email='joao.silva@teste.com')
                                          AND id_avaliado  = (SELECT id FROM usuarios WHERE email='maria.santos@teste.com'))
BEGIN
    INSERT INTO avaliacoes (id_avaliador, id_avaliado, nota, comentario, data)
    SELECT av.id, ad.id, 5, 'Serviço de limpeza perfeito. Recomendo a todos!', GETDATE()
    FROM usuarios av, usuarios ad
    WHERE av.email = 'joao.silva@teste.com' AND ad.email = 'maria.santos@teste.com';
END
GO

IF NOT EXISTS (SELECT 1 FROM avaliacoes WHERE id_avaliador = (SELECT id FROM usuarios WHERE email='cliente@teste.com')
                                          AND id_avaliado  = (SELECT id FROM usuarios WHERE email='maria.santos@teste.com'))
BEGIN
    INSERT INTO avaliacoes (id_avaliador, id_avaliado, nota, comentario, data)
    SELECT av.id, ad.id, 4, 'Muito boa profissional, caprichosa no serviço.', DATEADD(day, -3, GETDATE())
    FROM usuarios av, usuarios ad
    WHERE av.email = 'cliente@teste.com' AND ad.email = 'maria.santos@teste.com';
END
GO

-- Carlos Tech: avaliação
IF NOT EXISTS (SELECT 1 FROM avaliacoes WHERE id_avaliador = (SELECT id FROM usuarios WHERE email='cliente@teste.com')
                                          AND id_avaliado  = (SELECT id FROM usuarios WHERE email='carlos.tech@teste.com'))
BEGIN
    INSERT INTO avaliacoes (id_avaliador, id_avaliado, nota, comentario, data)
    SELECT av.id, ad.id, 5, 'Resolveu meu problema em minutos. Ótimo atendimento!', DATEADD(day, -7, GETDATE())
    FROM usuarios av, usuarios ad
    WHERE av.email = 'cliente@teste.com' AND ad.email = 'carlos.tech@teste.com';
END
GO

-- Ana Fitness: avaliação
IF NOT EXISTS (SELECT 1 FROM avaliacoes WHERE id_avaliador = (SELECT id FROM usuarios WHERE email='joao.silva@teste.com')
                                          AND id_avaliado  = (SELECT id FROM usuarios WHERE email='ana.personal@teste.com'))
BEGIN
    INSERT INTO avaliacoes (id_avaliador, id_avaliado, nota, comentario, data)
    SELECT av.id, ad.id, 5, 'Ana é incrível! Resultados visíveis em 2 semanas.', DATEADD(day, -5, GETDATE())
    FROM usuarios av, usuarios ad
    WHERE av.email = 'joao.silva@teste.com' AND ad.email = 'ana.personal@teste.com';
END
GO

PRINT 'Dados de teste inseridos com sucesso!';
