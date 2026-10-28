# Système de Management et de Suivi des Marchés de Matériels et d'Équipements — EPT

Projet Full-Stack conçu et développé pour l'**École Polytechnique de Thiès (EPT)**.

---

## 🏛️ 1. Présentation Générale

Ce système répond à l'intégralité du cahier des charges de l'EPT pour la gestion informatisée des marchés publics de matériels :
- **Recueil et traçabilité des expressions de besoins** émises par les chefs de départements/services.
- **Validation, arbitrage et regroupement en marchés et lots** par le Service des Achats.
- **Gestion du référentiel des fournisseurs** (agréments, NINEA, domaines d'intervention, activation/désactivation).
- **Collecte, analyse comparative des offres tarifaires et attributions sécurisées**.
- **Suivi rigoureux des livraisons physiques** et du taux d'exécution des marchés en temps réel.
- **Pilotage décisionnel pour la Direction** avec tableaux de bord analytiques et exports automatisés (Excel XLSX et CSV).
- **Assistant Chatbot intelligent** basé sur le traitement du langage naturel (NLP) pour interroger le système en langage naturel.

---

## 🛠️ 2. Architecture et Technologies

### Backend (Microservices Spring Boot 3.2.5 & Java 21)
- **`auth-service` (Port 8081)** : Gestion des comptes utilisateurs, départements, authentification JWT, hashage BCrypt.
- **`procurement-service` (Port 8082)** : Cœur métier (Besoins, Marchés, Lots, Offres, Livraisons, Historique des statuts, Statistiques).
- **`supplier-service` (Port 8083)** : Référentiel centralisé des prestataires et fournisseurs avec contrôle d'unicité NINEA.
- **`dashboard-service` (Port 8084)** : Agrégateur de données décisionnelles et moteur de génération de rapports Excel (Apache POI).
- **`chatbot-service` (Port 8085)** : Assistant conversationnel avec moteur NLP local sans hallucination.
- **`api-gateway` (Port 8080)** : Passerelle API réactive (Spring Cloud Gateway) gérant le routage et le CORS.
- **`common`** : Module partagé (Filtre JWT, Sécurité, DTOs de réponse standardisés, Exceptions métier et Gestionnaire global d'erreurs).

### Frontend (Angular 20 & TypeScript)
- Composants autonomes (**Standalone Components**).
- Architecture réactive avec **Signals** et **Reactive Forms**.
- **Intercepteur HTTP JWT** avec injection automatique du token Bearer et gestion des codes 401/403.
- **Guards de navigation** (`authGuard`, `roleGuard`).
- Interface moderne, sobre, responsive et **100% en français**, respectant la charte institutionnelle de l'EPT.

### Base de données
- **MySQL 8.0** avec bases dédiées par microservice (`ept_auth`, `ept_procurement`, `ept_supplier`).
- Configuration Docker Compose pour déploiement en une seule commande.

---

## 🚀 3. Guide de Démarrage Rapide

### Prérequis
- **Java 21 LTS** (`java -version`)
- **Maven 3.9+** (`mvn -v`)
- **Node.js v20+** et **npm** (`node -v`, `npm -v`)
- **MySQL 8.0** ou **Docker / Docker Compose**

---

### Étape 1 : Démarrage des Bases de Données MySQL

#### Option A : Via Docker Compose (Recommandé)
À la racine du projet :
```bash
docker compose up -d
```
Les bases de données `ept_auth`, `ept_procurement` et `ept_supplier` seront automatiquement initialisées avec les droits appropriés.

#### Option B : Sur votre MySQL local
Connectez-vous à votre serveur MySQL et exécutez le script d'initialisation :
```bash
mysql -u root -p < database/init/01-create-databases.sql
```

---

### Étape 2 : Compilation et Démarrage du Backend

1. **Compiler l'ensemble des microservices :**
   ```bash
   mvn clean install -DskipTests
   ```

2. **Démarrer les services (dans des terminaux séparés ou en arrière-plan) :**
   - **Auth Service :**
     ```bash
     cd backend/auth-service && mvn spring-boot:run
     ```
   - **Supplier Service :**
     ```bash
     cd backend/supplier-service && mvn spring-boot:run
     ```
   - **Procurement Service :**
     ```bash
     cd backend/procurement-service && mvn spring-boot:run
     ```
   - **Dashboard Service :**
     ```bash
     cd backend/dashboard-service && mvn spring-boot:run
     ```
   - **Chatbot Service :**
     ```bash
     cd backend/chatbot-service && mvn spring-boot:run
     ```
   - **API Gateway :**
     ```bash
     cd backend/api-gateway && mvn spring-boot:run
     ```

---

### Étape 3 : Démarrage de l'Application Frontend

```bash
cd frontend
npm start
```
L'application web sera accessible sur : **`http://localhost:4200`**

---

## 👥 4. Comptes de Démonstration Pré-configurés

Des comptes de test réalistes avec mot de passe initialisé sont automatiquement injectés au premier démarrage :

| Rôle Métier | Email | Mot de passe | Rôle & Attributions |
| :--- | :--- | :--- | :--- |
| **Chef de Service (Info)** | `chef.info@ept.sn` | `Chef@2024` | Émettre, suivre et modifier ses expressions de besoins informatiques. |
| **Chef de Service (Civil)** | `chef.civil@ept.sn` | `Chef@2024` | Émettre et suivre les demandes du département Génie Civil. |
| **Chef de Service (Élec)** | `chef.elec@ept.sn` | `Chef@2024` | Émettre et suivre les demandes du département Génie Électrique. |
| **Service des Achats** | `achats@ept.sn` | `Achats@2024` | Valider les besoins, composer les marchés/lots, gérer les fournisseurs et réceptions. |
| **Direction** | `direction@ept.sn` | `Direction@2024` | Consulter les indicateurs clés de performance (KPI) et exporter les rapports. |
| **Administrateur** | `admin@ept.sn` | `Admin@2024` | Gestion technique globale et administration des comptes utilisateurs. |

> *Note : Des boutons de connexion rapide en un clic sont présents sur l'écran d'authentification pour faciliter vos tests et démonstrations.*

---

## 📚 5. Documentation Complète du Projet

Toute la documentation d'ingénierie logicielle se trouve dans le répertoire `docs/` :
- **[Spécifications Techniques et Matrice de Traçabilité](docs/SPECIFICATIONS_TECHNIQUES.md)** : Couverture exhaustive des exigences du cahier des charges reliées aux modules, contrôleurs, écrans et tests.
- **[Diagrammes UML de Conception](docs/UML_DIAGRAMMES.md)** : Diagrammes de cas d'utilisation, classes du domaine, séquences et architecture système.
- **[Guide d'Installation et de Déploiement](docs/GUIDE_INSTALLATION_DEMARRAGE.md)** : Instructions détaillées de déploiement pas-à-pas.
- **[Guide des Tests Automatisés et Scénarios Métier](docs/GUIDE_TESTS.md)** : Déroulement des 11 scénarios de validation fonctionnelle de bout en bout.

---

## 🧪 6. Exécution des Tests Automatisés

Pour lancer la suite de tests unitaires et d'intégration Spring Boot & Mockito :
```bash
mvn test
```

---

## 🏢 7. Contact et Mentions Légales

- **Institution :** École Polytechnique de Thiès (EPT)
- **Projet :** Système de Management et de Suivi des Marchés de Matériels et d'Équipements
- **Année :** 2024-2025
