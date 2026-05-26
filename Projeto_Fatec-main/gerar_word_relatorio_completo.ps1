$inputPath = "c:\Users\paimn\Desktop\trabalho fatec\Projeto_Fatec-main\relatorio_extensao_completo_2026.md"
$outputPath = "c:\Users\paimn\Desktop\trabalho fatec\Projeto_Fatec-main\Relatorio_Projeto_Extensao_INFO4P_2026-1_COMPLETO.docx"

if (-not (Test-Path $inputPath)) {
    Write-Error "Arquivo de entrada nao encontrado: $inputPath"
    exit 1
}

$lines = Get-Content -Path $inputPath -Encoding UTF8
$content = [string]::Join("`r`n", $lines)

$word = $null
$doc = $null

try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $doc = $word.Documents.Add()

    $doc.Content.Text = $content

    $doc.SaveAs([ref]$outputPath, [ref]16)
    $doc.Close()
    $word.Quit()

    Write-Output "OK: Documento completo gerado em $outputPath"
}
catch {
    if ($doc -ne $null) { $doc.Close() }
    if ($word -ne $null) { $word.Quit() }
    Write-Error "Falha ao gerar documento: $($_.Exception.Message)"
    exit 1
}
