$ErrorActionPreference = "Stop"

$baseUrl = "http://localhost:8081"
$adminId = 4
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email = "cascade.$stamp@teste.com"
$cpf = (10000000000 + ($stamp % 89999999999)).ToString()

$registerBody = @{
    nome = "Usuario Cascade Test"
    email = $email
    senha = "Senha@123"
    cpf = $cpf
    telefone = "11999999999"
    endereco = "Rua Teste, 123"
    cidade = "Sao Paulo"
    estado = "SP"
    cep = "01001000"
    tipo = "cliente"
} | ConvertTo-Json

$newUser = Invoke-RestMethod -Uri "$baseUrl/usuario/register" -Method Post -ContentType "application/json" -Body $registerBody
Write-Output "NEW_USER_ID=$($newUser.id)"

$pedidoBody = @{
    usuario = @{ id = $newUser.id }
    titulo = "Pedido teste cascade"
    descricao = "Descricao teste"
    localizacao = "Sao Paulo"
    status = "ABERTO"
} | ConvertTo-Json -Depth 5

$pedido = Invoke-RestMethod -Uri "$baseUrl/pedido" -Method Post -ContentType "application/json" -Body $pedidoBody
Write-Output "NEW_PEDIDO_ID=$($pedido.id)"

$promoted = Invoke-RestMethod -Uri "$baseUrl/admin/usuario/$($newUser.id)/status?adminId=$adminId" -Method Put -ContentType "application/json" -Body (@{ status = 10 } | ConvertTo-Json)
Write-Output "PROMOTED_STATUS=$($promoted.status)"

$demoted = Invoke-RestMethod -Uri "$baseUrl/admin/usuario/$($newUser.id)/status?adminId=$adminId" -Method Put -ContentType "application/json" -Body (@{ status = 2 } | ConvertTo-Json)
Write-Output "DEMOTED_STATUS=$($demoted.status)"

$deleteResp = Invoke-WebRequest -Uri "$baseUrl/admin/usuario/$($newUser.id)?adminId=$adminId" -Method Delete -UseBasicParsing
Write-Output "DELETE_STATUS=$($deleteResp.StatusCode)"

try {
    $checkUser = Invoke-WebRequest -Uri "$baseUrl/usuario/$($newUser.id)" -Method Get -UseBasicParsing
    Write-Output "CHECK_USER_STATUS=$($checkUser.StatusCode)"
} catch {
    if ($_.Exception.Response) {
        Write-Output "CHECK_USER_STATUS=$([int]$_.Exception.Response.StatusCode)"
    } else {
        Write-Output "CHECK_USER_STATUS=ERR"
    }
}

try {
    $checkPedido = Invoke-WebRequest -Uri "$baseUrl/pedido/$($pedido.id)" -Method Get -UseBasicParsing
    Write-Output "CHECK_PEDIDO_STATUS=$($checkPedido.StatusCode)"
} catch {
    if ($_.Exception.Response) {
        Write-Output "CHECK_PEDIDO_STATUS=$([int]$_.Exception.Response.StatusCode)"
    } else {
        Write-Output "CHECK_PEDIDO_STATUS=ERR"
    }
}
