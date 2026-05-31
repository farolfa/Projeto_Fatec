$ErrorActionPreference = "SilentlyContinue"

try {
    $resp = Invoke-WebRequest -Uri "http://localhost:8081/admin/usuario/11?adminId=4" -Method Delete -UseBasicParsing
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

try {
    $check = Invoke-WebRequest -Uri "http://localhost:8081/usuario/11" -Method Get -UseBasicParsing
    Write-Output "CHECK_USER=$($check.StatusCode)"
} catch {
    if ($_.Exception.Response) {
        Write-Output "CHECK_USER=$([int]$_.Exception.Response.StatusCode)"
    } else {
        Write-Output "CHECK_USER=ERR"
    }
}
