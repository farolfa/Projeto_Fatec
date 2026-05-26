$inputPath = "c:\Users\paimn\Desktop\trabalho fatec\Projeto_Fatec-main\relatorio_extensao_abnt_qa_2026.md"
$outputPath = "c:\Users\paimn\Desktop\trabalho fatec\Projeto_Fatec-main\Relatorio_Projeto_Extensao_INFO4P_2026-1_ABNT_QA.docx"

if (-not (Test-Path $inputPath)) {
    Write-Error "Arquivo de entrada nao encontrado: $inputPath"
    exit 1
}

$lines = Get-Content -Path $inputPath -Encoding UTF8
$content = [string]::Join("`r`n", $lines)

$word = $null
$doc = $null

function Set-AbntPageSetup($document) {
    $cm = 28.3464567
    $document.PageSetup.TopMargin = 3 * $cm
    $document.PageSetup.LeftMargin = 3 * $cm
    $document.PageSetup.BottomMargin = 2 * $cm
    $document.PageSetup.RightMargin = 2 * $cm
}

try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $doc = $word.Documents.Add()

    Set-AbntPageSetup -document $doc

    $normalStyle = $doc.Styles.Item("Normal")
    $normalStyle.Font.Name = "Times New Roman"
    $normalStyle.Font.Size = 12
    $normalStyle.ParagraphFormat.Alignment = 3 # Justificado
    $normalStyle.ParagraphFormat.LineSpacingRule = 1 # 1,5 linhas
    $normalStyle.ParagraphFormat.LineSpacing = 18
    $normalStyle.ParagraphFormat.FirstLineIndent = 35.43 # 1,25 cm
    $normalStyle.ParagraphFormat.SpaceBefore = 0
    $normalStyle.ParagraphFormat.SpaceAfter = 0

    $doc.Content.Text = $content

    $doc.SaveAs([ref]$outputPath, [ref]16)
    $doc.Close()
    $word.Quit()

    Write-Output "OK: Documento ABNT gerado em $outputPath"
}
catch {
    if ($doc -ne $null) { $doc.Close() }
    if ($word -ne $null) { $word.Quit() }
    Write-Error "Falha ao gerar documento ABNT: $($_.Exception.Message)"
    exit 1
}
