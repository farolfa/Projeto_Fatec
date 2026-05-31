$ErrorActionPreference = "SilentlyContinue"

try {
    $resp = Invoke-WebRequest -Uri "http://localhost:8080/admin/usuario/11?adminId=4" -Method Delete -UseBasicParsing
    Write-Output "STATUS=$($resp.StatusCode)"
    Write-Output "BODY=$($resp.Content)"
} catch {
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $body = $reader.ReadToEnd()
        Write-Output "STATUS=$([int]$_.Exception.Response.StatusCode)"
        Write-Output "BODY=$body"
    } else {
        Write-Output "ERR=$($_.Exception.Message)"
    }
}
