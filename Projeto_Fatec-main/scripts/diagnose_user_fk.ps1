Add-Type -AssemblyName System.Data

$userId = 11
$connString = "Server=localhost,1433;Database=Faztudoja;User ID=Teste;Password=Teste@2026Sql!;Encrypt=True;TrustServerCertificate=True;"

$conn = New-Object System.Data.SqlClient.SqlConnection($connString)
$conn.Open()

$fkQuery = @"
SELECT
    t.name AS referencing_table,
    c.name AS referencing_column
FROM sys.foreign_key_columns fkc
INNER JOIN sys.tables rt ON rt.object_id = fkc.referenced_object_id
INNER JOIN sys.tables t ON t.object_id = fkc.parent_object_id
INNER JOIN sys.columns c ON c.object_id = fkc.parent_object_id AND c.column_id = fkc.parent_column_id
WHERE rt.name = 'usuarios'
ORDER BY t.name, c.name;
"@

$cmd = $conn.CreateCommand()
$cmd.CommandText = $fkQuery
$reader = $cmd.ExecuteReader()

$refs = @()
while ($reader.Read()) {
    $refs += [PSCustomObject]@{
        Table = $reader["referencing_table"].ToString()
        Column = $reader["referencing_column"].ToString()
    }
}
$reader.Close()

Write-Output "FK_REFERENCES_TO_USUARIOS="
$refs | Format-Table -AutoSize | Out-String | Write-Output

foreach ($r in $refs) {
    $countCmd = $conn.CreateCommand()
    $countCmd.CommandText = "SELECT COUNT(1) FROM [$($r.Table)] WHERE [$($r.Column)] = @id"
    $null = $countCmd.Parameters.Add("@id", [System.Data.SqlDbType]::BigInt)
    $countCmd.Parameters["@id"].Value = $userId
    $count = [int]$countCmd.ExecuteScalar()
    if ($count -gt 0) {
        Write-Output ("BLOCKER table={0} column={1} count={2}" -f $r.Table, $r.Column, $count)
    }
}

$conn.Close()
