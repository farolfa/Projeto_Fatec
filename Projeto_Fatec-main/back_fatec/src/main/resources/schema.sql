IF EXISTS(
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.usuarios')
      AND name = 'ativo'
)
BEGIN
    ALTER TABLE dbo.usuarios DROP COLUMN ativo;
END
