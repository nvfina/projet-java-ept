# Cahier de Spécifications Techniques

## Système de Management et de Suivi des Marchés de Matériels et d’Équipements
### École Polytechnique de Thiès (EPT)
**Année Académique : 2025-2026**
**Master 1 Ingénierie Logicielle et Intelligence Artificielle**

---

## 1. Présentation Générale

Le projet consiste en une plateforme web complète permettant de dématérialiser, centraliser et fiabiliser la gestion de tout le cycle de vie des acquisitions de matériels et d'équipements de l'EPT.

### 1.1 Objectifs Clés
- **Centralisation** de l'expression des besoins des différents départements universitaires (Informatique, Génie Civil, Génie Électrique, Génie Mécanique).
- **Structuration et traçabilité** des marchés publics sous forme de lots.
- **Référentiel fournisseurs** certifié avec NINEA et domaines d'intervention.
- **Suivi d'exécution** en temps réel des livraisons et des taux d'avancement.
- **Aide à la décision** pour la Direction via tableaux de bord et rapports exportables.
- **Recherche conversationnelle assistée par IA** via un agent Chatbot en langage naturel.

---

## 2. Architecture Technique

### 2.1 Découpage en Microservices
La solution adopte une architecture orientée microservices (SOA/Microservices) avec responsabilités isolées :

1. **API Gateway (Port 8080)** :
   - Point d'entrée HTTP unique pour le frontend Angular.
   - Routage réactif et sécurisé vers chaque microservice.
   - Gestion centralisée de la politique CORS.

2. **Auth Service (Port 8081)** :
   - Authentification basée sur les jetons JWT (HMAC-SHA256).
   - Gestion des utilisateurs et hachage BCrypt des mots de passe.
   - Contrôle d'accès basé sur les rôles (RBAC).
   - Gestion des départements et services de l'EPT.

3. **Procurement Service (Port 8082)** :
   - Cœur métier de la plateforme.
   - Gestion des Expressions de Besoins et leurs lignes d'articles.
   - Création des Marchés et constitution des Lots.
   - Réception et évaluation comparative des Offres tarifaires fournisseurs.
   - Attribution formelle des lots aux prestataires.
   - Enregistrement des Livraisons et calcul automatique du taux d'exécution.
   - Historique d'audit des statuts.

4. **Supplier Service (Port 8083)** :
   - Référentiel des prestataires et fournisseurs.
   - Gestion des fiches détaillées (Raison sociale, NINEA, coordonnées, domaine).
   - Activation / désactivation avec intégrité référentielle.

5. **Dashboard Service (Port 8084)** :
   - Agrégation des statistiques multi-services.
   - Calcul des indicateurs clés (KPIs) en temps réel.
   - Moteur d'exportation de rapports décisionnels aux formats Excel (.xlsx) et CSV.

6. **Chatbot Service (Port 8085)** :
   - Moteur NLP modulaire d'analyse sémantique et de reconnaissance d'intentions.
   - Requêtage contrôlé des API pour restituer des données exactes et certifiées.
   - Aucun recours obligatoire à des API cloud payantes.

---

## 3. Matrice de Traçabilité des Exigences

| Exigence Métier | Module Backend | Route API Principale | Composant Frontend | Test Automatisé |
|-----------------|----------------|----------------------|--------------------|-----------------|
| Authentification JWT sécurisée | auth-service | `POST /api/auth/login` | `LoginComponent` | `AuthControllerTest` |
| Profil utilisateur connecté | auth-service | `GET /api/auth/me` | `ProfilComponent` | `UtilisateurServiceTest` |
| Création Expression Besoin | procurement-service | `POST /api/besoins` | `BesoinCreateComponent` | `BesoinServiceTest` |
| Validation / Rejet Demande | procurement-service | `PUT /api/besoins/{id}/valider` | `BesoinDetailComponent` | `BesoinValidationTest` |
| Montage d'un Marché en Lots | procurement-service | `POST /api/marches` | `MarcheCreateComponent` | `MarcheServiceTest` |
| Annuaire Fournisseurs | supplier-service | `CRUD /api/fournisseurs` | `FournisseurListComponent` | `FournisseurServiceTest` |
| Offres tarifaires & Comparatif | procurement-service | `POST /api/offres` | `OffreCompareComponent` | `OffreServiceTest` |
| Attribution formelle de lot | procurement-service | `PUT /api/lots/{id}/attribuer` | `LotAttributionComponent` | `LotAttributionTest` |
| Suivi des Livraisons | procurement-service | `POST /api/livraisons` | `LivraisonCreateComponent`| `LivraisonServiceTest` |
| Indicateurs & KPI Direction | dashboard-service | `GET /api/dashboard/indicateurs` | `DashboardComponent` | `DashboardServiceTest` |
| Export Rapports Excel / CSV | dashboard-service | `GET /api/rapports/marches/excel` | `RapportComponent` | `RapportExportTest` |
| Recherche Chatbot NLP | chatbot-service | `POST /api/chatbot/message` | `ChatbotComponent` | `ChatbotServiceTest` |

---

## 4. Modèle de Données et Règles d'Intégrité

### 4.1 Principales Tables Relationnelles (MySQL)
1. `utilisateurs` : id, nom, prenom, email (UNIQUE), mot_de_passe (BCrypt), role, service_nom, actif.
2. `services` : id, nom (UNIQUE), description, actif.
3. `expressions_besoin` : id, reference (UNIQUE), service_demandeur, demandeur_id, statut, urgente, commentaire_decision.
4. `lignes_besoin` : id, expression_besoin_id, designation, quantite_demandee, unite, estimation_prix.
5. `marches` : id, reference (UNIQUE), objet, statut, budget_previsionnel, montant_total, date_lancement, date_cloture.
6. `lots` : id, numero, designation, marche_id, statut, montant_estime, montant_attribue, fournisseur_id, taux_execution.
7. `lignes_lot` : id, lot_id, ligne_besoin_id, designation, quantite, prix_unitaire, montant_total.
8. `fournisseurs` : id, raison_sociale (UNIQUE), ninea (UNIQUE), telephone, email, domaine_intervention, actif.
9. `offres_tarifaires` : id, lot_id, fournisseur_id, statut, montant_total, devise.
10. `lignes_offre` : id, offre_id, designation, quantite, prix_unitaire, montant_ligne.
11. `livraisons` : id, lot_id, date_livraison, quantite_livree, designation, conforme, receptionnaire_id.
12. `historique_statuts` : id, entite_type, entite_id, ancien_statut, nouveau_statut, date_changement, utilisateur_id.

### 4.2 Règles Métier d'Intégrité
- **Unicité** garantie pour l'email utilisateur, le NINEA et la raison sociale des fournisseurs, et les références d'expression de besoin et de marché.
- **Calculs côté serveur** : Les totaux des offres et des marchés sont calculés strictement sur le backend.
- **Plafond de livraison** : La somme des livraisons conformes ne peut pas dépasser la quantité totale attribuée sur un lot.
- **Contrôle de statut** : Un besoin ne peut être modifié ou annulé que s'il est au statut `EN_ATTENTE`.
