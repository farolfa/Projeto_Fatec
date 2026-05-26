-- =========================================================
-- LIMPEZA DO BANCO: remove clientes/prestadores, mantém admin
-- Remove tabelas obsoletas (Agenda, Contrato, Pagamento, Oferta)
-- Rodar no SQL Server Management Studio no banco Faztudoja
-- =========================================================
USE Faztudoja;
GO

-- =========================================================
-- PARTE 1: LIMPAR DADOS DE CLIENTES E PRESTADORES
-- Ordem: filhos primeiro, depois pai (FK)
-- =========================================================

-- Avaliações feitas por ou para não-admins
DELETE FROM avaliacoes
WHERE id_avaliador IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN')
   OR id_avaliado  IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Favoritos
DELETE FROM favoritos
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Notificações lidas externas
DELETE FROM notificacoes_lidas_externas
WHERE usuario_id IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Notificações
DELETE FROM notificacoes
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Respostas de ticket
DELETE FROM respostas_ticket
WHERE id_ticket IN (
    SELECT id FROM tickets_suporte
    WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN')
);

-- Tickets de suporte
DELETE FROM tickets_suporte
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Mensagens (depende de participantes — apagar antes)
DELETE FROM mensagens;

-- Participantes
DELETE FROM participantes
WHERE id_user_cliente IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN')
   OR id_user_prestador IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Propostas
DELETE FROM propostas
WHERE id_pedido IN (
    SELECT id FROM pedidos
    WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN')
)
OR id_prestador IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Pedidos
DELETE FROM pedidos
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Serviços oferecidos por prestadores
DELETE FROM servicos_oferecidos
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Telefones ligados a usuários não-admins
DELETE FROM telefones
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Endereços ligados a usuários não-admins
DELETE FROM enderecos
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo != 'ADMIN');

-- Tabelas de herança: cliente e prestador (JOINED inheritance)
DELETE FROM cliente
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo = 'CLIENTE');

DELETE FROM prestador
WHERE id_usuario IN (SELECT id FROM usuarios WHERE tipo = 'PRESTADOR');

-- Usuários (clientes e prestadores)
DELETE FROM usuarios
WHERE tipo != 'ADMIN';

PRINT 'Dados de clientes e prestadores removidos. Admin mantido.';
GO

-- =========================================================
-- PARTE 2: REMOVER TABELAS OBSOLETAS (se ainda existirem)
-- Agenda, Contrato, Pagamento, Oferta foram deletados do código
-- =========================================================

IF OBJECT_ID('agendas', 'U') IS NOT NULL
    DROP TABLE agendas;

IF OBJECT_ID('contratos', 'U') IS NOT NULL
    DROP TABLE contratos;

IF OBJECT_ID('pagamentos', 'U') IS NOT NULL
    DROP TABLE pagamentos;

IF OBJECT_ID('ofertas', 'U') IS NOT NULL
    DROP TABLE ofertas;

PRINT 'Tabelas obsoletas removidas (se existiam).';
GO

PRINT '=== Limpeza concluída com sucesso! ===';
