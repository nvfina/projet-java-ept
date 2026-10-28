# Guide d'Installation et de Démarrage

## Système de Management et de Suivi des Marchés de l'EPT

Ce guide fournit la procédure complète pour configurer, initialiser et démarrer l'ensemble des composants du système.

---

## 1. Prérequis Système

| Composant | Version Minimale Recommandée | Commande de Vérification |
|-----------|------------------------------|--------------------------|
| **Java (JDK)** | 21 LTS | `java -version` |
| **Apache Maven** | 3.9+ | `mvn -version` |
| **Node.js** | 20+ ou 22 LTS | `node -v` |
| **npm** | 10+ | `npm -v` |
| **Angular CLI** | 20+ | `npx ng version` |
| **Docker & Docker Compose** (ou MySQL local) | 24+ / Compose v2 | `docker --version` |

---

## 2. Configuration de la Base de Données (MySQL)

Deux options sont disponibles pour la persistance des données.

### Option A : Avec Docker Compose (Recommandé & Automatique)
Une seule commande crée le conteneur MySQL et initialise automatiquement les 3 bases de données (`ept_auth`, `ept_procurement`, `ept_supplier`) :
```bash
docker compose up -d mysql
```

### Option B : Avec un serveur MySQL local déjà installé
Si vous utilisez un serveur MySQL déjà actif sur votre machine locale :
1. Connectez-vous à MySQL :
   ```sql
   mysql -u root -p
   ```
2. Exécutez le script d'initialisation :
   ```sql
   CREATE DATABASE IF NOT EXISTS ept_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE IF NOT EXISTS ept_procurement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE IF NOT EXISTS ept_supplier CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Assurez-vous que le mot de passe root correspond à celui configuré dans `.env` (par défaut `ept_root_2024` ou votre mot de passe habituel). Vous pouvez surcharger la variable :
   ```bash
   $env:MYSQL_ROOT_PASSWORD="votre_mot_de_passe"
   ```

---

## 3. Démarrage des Microservices Backend

### Étape 1 : Compiler l'ensemble des modules
Depuis la racine du projet (`ept-management-marches`) :
```bash
mvn clean install -DskipTests
```

### Étape 2 : Lancer les services (Chacun dans un terminal dédié ou en tâche de fond)

1. **Service Authentification (Port 8081)** :
   ```bash
   cd backend/auth-service
   mvn spring-boot:run
   ```

2. **Service Fournisseurs (Port 8083)** :
   ```bash
   cd backend/supplier-service
   mvn spring-boot:run
   ```

3. **Service Approvisionnement (Procurement) (Port 8082)** :
   ```bash
   cd backend/procurement-service
   mvn spring-boot:run
   ```

4. **Service Tableau de Bord (Port 8084)** :
   ```bash
   cd backend/dashboard-service
   mvn spring-boot:run
   ```

5. **Service Chatbot Conversationnel (Port 8085)** :
   ```bash
   cd backend/chatbot-service
   mvn spring-boot:run
   ```

6. **API Gateway (Port 8080)** :
   ```bash
   cd backend/api-gateway
   mvn spring-boot:run
   ```

---

## 4. Démarrage de l'Application Frontend Angular

Depuis la racine du projet :
```bash
cd frontend
npm install
npm start
```
L'application web est accessible sur : **http://localhost:4200**

---

## 5. Comptes de Démonstration Pré-enregistrés

Au premier lancement, le système initialise automatiquement les comptes suivants avec leurs rôles respectifs :

| Rôle Métier | Identifiant (Email) | Mot de passe | Rôle & Permissions |
|-------------|---------------------|--------------|-------------------|
| **ADMINISTRATEUR** | `admin@ept.sn` | `Admin@2024` | Administration complète, création de comptes et départements |
| **CHEF_SERVICE** | `chef.info@ept.sn` | `Chef@2024` | Chef dép. Informatique - Saisie et suivi de besoins |
| **CHEF_SERVICE** | `chef.civil@ept.sn` | `Chef@2024` | Chef dép. Génie Civil - Saisie et suivi de besoins |
| **CHEF_SERVICE** | `chef.elec@ept.sn` | `Chef@2024` | Chef dép. Génie Électrique - Saisie et suivi de besoins |
| **SERVICE_ACHATS** | `achats@ept.sn` | `Achats@2024` | Validation, Marchés, Lots, Fournisseurs, Livraisons |
| **DIRECTION** | `direction@ept.sn` | `Direction@2024` | Tableaux de bord, Indicateurs, Rapports décisionnels |

---

## 6. Accès aux Documentations Swagger / OpenAPI

Chaque microservice expose sa documentation interactive :

- **Auth Service** : `http://localhost:8081/api/auth/swagger-ui.html`
- **Supplier Service** : `http://localhost:8083/api/fournisseurs/swagger-ui.html`
- **Procurement Service** : `http://localhost:8082/api/procurement/swagger-ui.html`
- **Dashboard Service** : `http://localhost:8084/api/dashboard/swagger-ui.html`
- **Chatbot Service** : `http://localhost:8085/api/chatbot/swagger-ui.html`
