# serve-frontend.ps1
# Runs the frontend separately from the backend.
# Backend stays at https://peershpere-production.up.railway.app
# (see js/config.js). Serves the frontend from its own origin.
#
# Usage:  powershell -ExecutionPolicy Bypass -File serve-frontend.ps1
# Open:   http://localhost:5500/landing.html
$port = 5500
$staticDir = Join-Path $PSScriptRoot "src\main\resources\static"

if (-not (Test-Path $staticDir)) {
    Write-Host "Static dir not found: $staticDir" -ForegroundColor Red
    exit 1
}

$python = Get-Command python -ErrorAction SilentlyContinue
if ($python) {
    Push-Location $staticDir
    Write-Host "Frontend: http://localhost:$port  (backend -> js/config.js)" -ForegroundColor Green
    python -m http.server $port
    Pop-Location
}
else {
    Write-Host "Python not found, using npx http-server" -ForegroundColor Yellow
    npx --yes http-server "$staticDir" -p $port -c-1
}