param(
    [int]$Duration = 60,  # Duration in seconds
    [int]$RequestsPerSecond = 5
)

$startTime = Get-Date
$endTime = $startTime.AddSeconds($Duration)

Write-Host "Generating traffic for $Duration seconds at $RequestsPerSecond requests per second..."
Write-Host "Start time: $startTime"
Write-Host "End time: $endTime"
Write-Host "Press Ctrl+C to stop"

$counter = 0

while ((Get-Date) -lt $endTime) {
    # Mix of GET and POST requests (70% GET, 30% POST)
    $requestType = Get-Random -Minimum 1 -Maximum 11
    
    if ($requestType -le 7) {
        # GET request
        try {
            Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get | Out-Null
            Write-Host "GET request #$counter successful" -ForegroundColor Green
        } catch {
            Write-Host "GET request #$counter failed: $_" -ForegroundColor Red
        }
    } else {
        # POST request
        $userId = Get-Random -Minimum 1000 -Maximum 9999
        $body = @{
            name = "TestUser$userId"
            email = "test$userId@example.com"
        } | ConvertTo-Json
        
        try {
            Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -ContentType "application/json" -Body $body | Out-Null
            Write-Host "POST request #$counter successful" -ForegroundColor Cyan
        } catch {
            Write-Host "POST request #$counter failed: $_" -ForegroundColor Red
        }
    }
    
    $counter++
    
    # Sleep to maintain the requests per second rate
    Start-Sleep -Milliseconds (1000 / $RequestsPerSecond)
}

Write-Host "Traffic generation completed. Total requests: $counter"
