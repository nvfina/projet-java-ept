# ==============================================================================
# Script de Démarrage Global (Backend + Frontend) - EPT Gestion des Marchés
# ==============================================================================

$root = $PSScriptRoot
if (-not $root) { 
    $root = "C:\Users\Nafissatou Niang\.gemini\antigravity\scratch\ept-management-marches" 
}

Clear-Host
Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "   SYSTEME DE GESTION DES MARCHES - ECOLE POLYTECHNIQUE DE THIES  " -ForegroundColor Yellow
Write-Host "           DEMARRAGE COMPLET DU PROJET (BACKEND + FRONTEND)       " -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Cyan

# 1. Démarrer le backend (MySQL + 6 microservices)
& "$root\demarrer-backend.ps1"

# 2. Démarrer le frontend Angular
Write-Host "`nLancement du Frontend Angular (Port 4200)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\frontend'; Write-Host '=== FRONTEND ANGULAR (4200) ===' -ForegroundColor Cyan; npm start"

Write-Host "`n==================================================================" -ForegroundColor Cyan
Write-Host " L'application EPT est prete !" -ForegroundColor Green
Write-Host " Accedez au portail web sur : http://localhost:4200" -ForegroundColor Yellow
Write-Host "==================================================================" -ForegroundColor Cyan

Start-Sleep -Seconds 5
# Ouvrir automatiquement le navigateur sur l'application
Start-Process "http://localhost:4200"
