USE [Faztudoja]
GO

/* ===========================================================
   PROCEDURE: transacao de abertura de pedido com endereco
   =========================================================== */
CREATE OR ALTER PROCEDURE dbo.sp_criar_pedido_com_endereco
    @id_usuario BIGINT,
    @titulo VARCHAR(160),
    @descricao VARCHAR(2000),
    @localizacao VARCHAR(160),
    @contato_nome VARCHAR(120) = NULL,
    @contato_email VARCHAR(160) = NULL,
    @contato_telefone VARCHAR(20) = NULL,
    @id_servico BIGINT = NULL,
    @rua VARCHAR(200),
    @cidade VARCHAR(120),
    @estado VARCHAR(2),
    @cep VARCHAR(8),
    @id_pedido_gerado BIGINT OUTPUT,
    @id_endereco_gerado BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM dbo.usuarios WHERE id = @id_usuario AND ativo = 1)
        BEGIN
            THROW 51001, 'Usuario inexistente ou inativo.', 1;
        END;

        IF @id_servico IS NOT NULL
           AND NOT EXISTS (SELECT 1 FROM dbo.servico_catalogo WHERE id = @id_servico)
        BEGIN
            THROW 51002, 'Servico do catalogo nao encontrado.', 1;
        END;

        INSERT INTO dbo.enderecos (
            cep, cidade, estado, rua, id_usuario
        )
        VALUES (
            @cep, @cidade, @estado, @rua, @id_usuario
        );

        SET @id_endereco_gerado = SCOPE_IDENTITY();

        INSERT INTO dbo.pedidos (
            cliente_confirmou_conclusao,
            contato_email,
            contato_nome,
            contato_telefone,
            data_criacao,
            descricao,
            localizacao,
            prestador_confirmou_conclusao,
            status,
            titulo,
            id_endereco,
            id_servico,
            id_usuario
        )
        VALUES (
            0,
            @contato_email,
            @contato_nome,
            @contato_telefone,
            SYSDATETIME(),
            @descricao,
            @localizacao,
            0,
            'ABERTO',
            @titulo,
            @id_endereco_gerado,
            @id_servico,
            @id_usuario
        );

        SET @id_pedido_gerado = SCOPE_IDENTITY();

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH
END;
GO

/* ===========================================================
   VIEW: consulta complexa para painel de pedidos
   =========================================================== */
CREATE OR ALTER VIEW dbo.vw_dashboard_pedidos
AS
SELECT
    p.id AS pedido_id,
    p.titulo,
    p.status AS status_pedido,
    p.data_criacao,
    p.localizacao,
    u_cliente.id AS cliente_id,
    u_cliente.nome AS cliente_nome,
    u_cliente.email AS cliente_email,
    e.cidade,
    e.estado,
    sc.id AS servico_id,
    sc.titulo AS servico_titulo,
    c.nome AS categoria_nome,
    COUNT(pr.id) AS total_propostas,
    SUM(CASE WHEN pr.status = 'ACEITA' THEN 1 ELSE 0 END) AS propostas_aceitas,
    CAST(MIN(pr.preco_proposto) AS DECIMAL(12, 2)) AS menor_preco_proposto,
    CAST(MAX(pr.preco_proposto) AS DECIMAL(12, 2)) AS maior_preco_proposto,
    CAST(AVG(CAST(pr.preco_proposto AS DECIMAL(12, 2))) AS DECIMAL(12, 2)) AS media_preco_proposto,
    CAST(AVG(CAST(av.nota AS DECIMAL(10, 2))) AS DECIMAL(10, 2)) AS media_avaliacao_prestadores
FROM dbo.pedidos p
INNER JOIN dbo.usuarios u_cliente ON u_cliente.id = p.id_usuario
LEFT JOIN dbo.enderecos e ON e.id = p.id_endereco
LEFT JOIN dbo.servico_catalogo sc ON sc.id = p.id_servico
LEFT JOIN dbo.categorias c ON c.id = sc.id_categoria
LEFT JOIN dbo.propostas pr ON pr.id_pedido = p.id
LEFT JOIN dbo.avaliacoes av ON av.id_avaliado = pr.id_prestador
GROUP BY
    p.id,
    p.titulo,
    p.status,
    p.data_criacao,
    p.localizacao,
    u_cliente.id,
    u_cliente.nome,
    u_cliente.email,
    e.cidade,
    e.estado,
    sc.id,
    sc.titulo,
    c.nome;
GO

/* ===========================================================
   TRIGGER: gera notificacao automatica ao inserir proposta
   =========================================================== */
CREATE OR ALTER TRIGGER dbo.trg_propostas_notificar_nova
ON dbo.propostas
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.notificacoes (
        data,
        lida,
        mensagem,
        tipo,
        id_usuario
    )
    SELECT
        SYSDATETIME(),
        0,
        CONCAT('Voce recebeu uma nova proposta para o pedido #', p.id, ' no valor de R$ ', CONVERT(VARCHAR(40), i.preco_proposto)),
        'NOVA_PROPOSTA',
        p.id_usuario
    FROM inserted i
    INNER JOIN dbo.pedidos p ON p.id = i.id_pedido;
END;
GO

/* ===========================================================
   FUNCTION (table-valued): prestadores por categoria
   =========================================================== */
CREATE OR ALTER FUNCTION dbo.fn_prestadores_por_categoria (@id_categoria BIGINT)
RETURNS TABLE
AS
RETURN
(
    SELECT
        u.id AS prestador_id,
        u.nome AS prestador_nome,
        u.email AS prestador_email,
        pr.especialidade,
        c.id AS categoria_id,
        c.nome AS categoria_nome,
        COUNT(DISTINCT so.id) AS total_servicos_oferecidos,
        CAST(AVG(CAST(so.preco_medio AS DECIMAL(12, 2))) AS DECIMAL(12, 2)) AS media_preco_medio,
        CAST(AVG(CAST(av.nota AS DECIMAL(10, 2))) AS DECIMAL(10, 2)) AS media_avaliacao,
        COUNT(av.id) AS total_avaliacoes
    FROM dbo.usuarios u
    INNER JOIN dbo.prestador pr ON pr.id_usuario = u.id
    INNER JOIN dbo.servicos_oferecidos so ON so.id_usuario = u.id
    INNER JOIN dbo.servico_catalogo sc ON sc.id = so.id_servico
    INNER JOIN dbo.categorias c ON c.id = sc.id_categoria
    LEFT JOIN dbo.avaliacoes av ON av.id_avaliado = u.id
    WHERE u.ativo = 1
      AND u.tipo = 'PRESTADOR'
      AND (@id_categoria IS NULL OR c.id = @id_categoria)
    GROUP BY
        u.id,
        u.nome,
        u.email,
        pr.especialidade,
        c.id,
        c.nome
);
GO
