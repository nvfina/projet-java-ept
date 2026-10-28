# ==============================================================================
# Script de Démarrage Automatique du Backend EPT - Gestion des Marchés
# ==============================================================================

$root = $PSScriptRoot
if (-not $root) { 
    $root = "C:\Users\Nafissatou Niang\.gemini\antigravity\scratch\ept-management-marches" 
}

Clear-Host
Write-Host "==================================================================" -ForegroundColor Cyan
Write-Host "   SYSTEME DE GESTION DES MARCHES - ECOLE POLYTECHNIQUE DE THIES  " -ForegroundColor Yellow
Write-Host "                   DEMARRAGE DU BACKEND COMPLET                   " -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Cyan

# Étape 1 : Démarrage du conteneur MySQL
Write-Host "`n[1/7] Verification et demarrage de la base de donnees MySQL..." -ForegroundColor Yellow
Set-Location $root
docker compose up -d mysql

Write-Host "Attente de l'initialisation de MySQL (5 secondes)..." -ForegroundColor Gray
Start-Sleep -Seconds 5

# Étape 2 : Lancement d'Auth Service
Write-Host "[2/7] Lancement de Auth Service (Port 8081)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\auth-service'; Write-Host '=== AUTH SERVICE (8081) ===' -ForegroundColor Green; mvn spring-boot:run"

Start-Sleep -Seconds 3

# Étape 3 : Lancement de Supplier Service
Write-Host "[3/7] Lancement de Supplier Service (Port 8083)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\supplier-service'; Write-Host '=== SUPPLIER SERVICE (8083) ===' -ForegroundColor Green; mvn spring-boot:run"

# Étape 4 : Lancement de Procurement Service
Write-Host "[4/7] Lancement de Procurement Service (Port 8082)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\procurement-service'; Write-Host '=== PROCUREMENT SERVICE (8082) ===' -ForegroundColor Green; mvn spring-boot:run"

# Étape 5 : Lancement de Dashboard Service
Write-Host "[5/7] Lancement de Dashboard Service (Port 8084)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\dashboard-service'; Write-Host '=== DASHBOARD SERVICE (8084) ===' -ForegroundColor Green; mvn spring-boot:run"

# Étape 6 : Lancement de Chatbot Service
Write-Host "[6/7] Lancement de Chatbot Service (Port 8085)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\chatbot-service'; Write-Host '=== CHATBOT SERVICE (8085) ===' -ForegroundColor Green; mvn spring-boot:run"

Start-Sleep -Seconds 3

# Étape 7 : Lancement de l'API Gateway
Write-Host "[7/7] Lancement de API Gateway (Port 8080)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\api-gateway'; Write-Host '=== API GATEWAY (8080) ===' -ForegroundColor Green; mvn spring-boot:run"

Write-Host "`n==================================================================" -ForegroundColor Cyan
Write-Host " Tous les microservices ont ete lances avec succes !" -ForegroundColor Green
Write-Host " - Passerelle API Gateway : http://localhost:8080" -ForegroundColor White
Write-Host " - Swagger Documentation  : http://localhost:8081/api/auth/swagger-ui.html" -ForegroundColor White
Write-Host "==================================================================" -ForegroundColor Cyan
