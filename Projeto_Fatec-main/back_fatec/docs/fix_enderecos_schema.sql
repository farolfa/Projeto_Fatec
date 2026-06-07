-- Remove legacy address columns from the enderecos table and ensure the identity seed is correct.
-- Execute this script in SQL Server after reviewing and backing up your database.

IF COL_LENGTH('dbo.enderecos', 'numero') IS NOT NULL
BEGIN
    ALTER TABLE dbo.enderecos DROP COLUMN numero;
END

IF COL_LENGTH('dbo.enderecos', 'bairro') IS NOT NULL
BEGIN
    ALTER TABLE dbo.enderecos DROP COLUMN bairro;
END

IF COL_LENGTH('dbo.enderecos', 'principal') IS NOT NULL
BEGIN
    ALTER TABLE dbo.enderecos DROP COLUMN principal;
END

-- Optional: reseed identity to the current maximum id.
DECLARE @maxId BIGINT;
SELECT @maxId = MAX(id) FROM dbo.enderecos;
IF @maxId IS NULL SET @maxId = 0;
DBCC CHECKIDENT ('dbo.enderecos', RESEED, @maxId);

SELECT 'Schema fix applied. Current enderecos columns:' AS Message;
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'enderecos'
ORDER BY ORDINAL_POSITION;
