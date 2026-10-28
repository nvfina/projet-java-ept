# ==============================================================================
# Script d'Arrêt du Backend EPT
# ==============================================================================

Write-Host "Arret des services Java / Spring Boot..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force

Write-Host "Arret du conteneur MySQL..." -ForegroundColor Yellow
$root = $PSScriptRoot
if (-not $root) { 
    $root = "C:\Users\Nafissatou Niang\.gemini\antigravity\scratch\ept-management-marches" 
}
Set-Location $root
docker compose stop mysql

Write-Host "Le backend EPT a ete completement arrete." -ForegroundColor Green
