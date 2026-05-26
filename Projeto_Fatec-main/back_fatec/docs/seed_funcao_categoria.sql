USE [Faztudoja]
GO

IF NOT EXISTS (SELECT 1 FROM dbo.categorias WHERE nome='Hidraulica')
    INSERT INTO dbo.categorias(nome) VALUES ('Hidraulica');
GO

DECLARE @cat BIGINT = (SELECT TOP 1 id FROM dbo.categorias WHERE nome='Hidraulica');

IF NOT EXISTS (
    SELECT 1
    FROM dbo.servico_catalogo
    WHERE titulo='Conserto de Encanamento' AND id_categoria=@cat
)
BEGIN
    INSERT INTO dbo.servico_catalogo(titulo, descricao, id_categoria)
    VALUES ('Conserto de Encanamento', 'Troca e manutencao', @cat);
END;
GO

DECLARE @cat BIGINT = (SELECT TOP 1 id FROM dbo.categorias WHERE nome='Hidraulica');
DECLARE @serv BIGINT = (
    SELECT TOP 1 id
    FROM dbo.servico_catalogo
    WHERE titulo='Conserto de Encanamento' AND id_categoria=@cat
);

IF NOT EXISTS (
    SELECT 1
    FROM dbo.servicos_oferecidos
    WHERE id_usuario=2 AND id_servico=@serv
)
BEGIN
    INSERT INTO dbo.servicos_oferecidos(descricao, preco_medio, id_servico, id_usuario)
    VALUES ('Servico especializado', 120.00, @serv, 2);
END;
GO

DECLARE @cat BIGINT = (SELECT TOP 1 id FROM dbo.categorias WHERE nome='Hidraulica');
SELECT TOP 10 * FROM dbo.fn_prestadores_por_categoria(@cat);
GO
