# ==============================================================================
# Script de Démarrage du Frontend Angular EPT
# ==============================================================================

$root = $PSScriptRoot
if (-not $root) { 
    $root = "C:\Users\Nafissatou Niang\.gemini\antigravity\scratch\ept-management-marches" 
}

Clear-Host
Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "       DEMARRAGE DU FRONTEND ANGULAR - EPT GESTION DES MARCHES     " -ForegroundColor Yellow
Write-Host "==================================================================" -ForegroundColor Cyan

Set-Location "$root\frontend"
Write-Host "Lancement du serveur de developpement Angular..." -ForegroundColor Green
Write-Host "L'application sera accessible sur : http://localhost:4200`n" -ForegroundColor Yellow

npm start
