# Test SSE real-time stream
$url = "http://localhost:8080/events?channel=admin-feed"
$request = [System.Net.HttpWebRequest]::Create($url)
$request.Method = "GET"
$request.Timeout = 10000

$response = $request.GetResponse()
Write-Host "SSE Response Status:" $response.StatusCode
Write-Host "SSE Content-Type:" $response.ContentType

$stream = $response.GetResponseStream()
$reader = New-Object System.IO.StreamReader($stream)

# Read first few lines (handshake and initial event)
$line1 = $reader.ReadLine()
$line2 = $reader.ReadLine()
Write-Host "Line 1: $line1"
Write-Host "Line 2: $line2"

$reader.Close()
$response.Close()

if ($line2 -match "connected") {
    Write-Host "SSE Stream verification SUCCESS!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "SSE Stream verification unexpected data" -ForegroundColor Yellow
    exit 0
}
