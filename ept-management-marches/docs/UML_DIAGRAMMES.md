# Modèles et Diagrammes UML

## Projet EPT : Système de Management et de Suivi des Marchés

---

## 1. Diagramme de Cas d'Utilisation Global

```mermaid
flowchart LR
    subgraph Acteurs
        CS["Chef de Service / Département"]
        SA["Service des Achats"]
        DIR["Direction EPT"]
        BOT["Système Chatbot (Acteur Secondaire)"]
        ADM["Administrateur Système"]
    end

    subgraph Authentification
        UC_AUTH["S'authentifier (Login/JWT)"]
        UC_PROF["Consulter son profil & mot de passe"]
    end

    subgraph Besoins
        UC_CREER_B["Saisir une expression de besoin"]
        UC_MODIF_B["Modifier/Annuler une demande (si EN_ATTENTE)"]
        UC_SUIVRE_B["Suivre le traitement de ses besoins"]
        UC_VALID_B["Valider / Rejeter une demande"]
    end

    subgraph Marches_Lots
        UC_MONTER_M["Monter un marché & créer les lots"]
        UC_OFFRES["Saisir les offres des fournisseurs"]
        UC_COMPARE["Comparer les offres tarifaires"]
        UC_ATTRIB["Attribuer un lot à un prestataire"]
        UC_LIVR["Enregistrer les livraisons & incidents"]
    end

    subgraph Fournisseurs
        UC_FOURN["Gérer l'annuaire fournisseurs (CRUD, Actif/Inactif)"]
    end

    subgraph Decision
        UC_DASH["Consulter le tableau de bord décisionnel"]
        UC_EXPORT["Gérer & Exporter les rapports (Excel/CSV)"]
    end

    subgraph Assistance
        UC_CHAT["Recherche conversationnelle par NLP"]
    end

    CS --> UC_AUTH
    CS --> UC_CREER_B
    CS --> UC_MODIF_B
    CS --> UC_SUIVRE_B
    CS --> UC_CHAT

    SA --> UC_AUTH
    SA --> UC_VALID_B
    SA --> UC_MONTER_M
    SA --> UC_OFFRES
    SA --> UC_COMPARE
    SA --> UC_ATTRIB
    SA --> UC_LIVR
    SA --> UC_FOURN
    SA --> UC_CHAT

    DIR --> UC_AUTH
    DIR --> UC_DASH
    DIR --> UC_EXPORT
    DIR --> UC_CHAT

    ADM --> UC_AUTH

    UC_CHAT <--> BOT
```

---

## 2. Diagramme de Classes Métier du Domaine

```mermaid
classDiagram
    class Utilisateur {
        +Long id
        +String nom
        +String prenom
        +String email
        +String motDePasse
        +String telephone
        +RoleEnum role
        +String serviceNom
        +boolean actif
        +LocalDateTime dateCreation
    }

    class ServiceDepartement {
        +Long id
        +String nom
        +String description
        +boolean actif
    }

    class ExpressionBesoin {
        +Long id
        +String reference
        +String serviceDemandeur
        +Long demandeurId
        +String demandeurNom
        +String description
        +String motif
        +StatutBesoin statut
        +boolean urgente
        +LocalDateTime dateCreation
        +LocalDateTime dateValidation
        +String commentaireDecision
    }

    class LigneBesoin {
        +Long id
        +String designation
        +String description
        +Integer quantiteDemandee
        +String unite
        +BigDecimal estimationPrix
    }

    class Marche {
        +Long id
        +String reference
        +String objet
        +String description
        +StatutMarche statut
        +BigDecimal budgetPrevisionnel
        +BigDecimal montantTotal
        +LocalDateTime dateCreation
        +LocalDateTime dateLancement
        +LocalDateTime dateCloture
    }

    class Lot {
        +Long id
        +Integer numero
        +String designation
        +String description
        +StatutLot statut
        +BigDecimal montantEstime
        +BigDecimal montantAttribue
        +Long fournisseurId
        +String fournisseurNom
        +LocalDateTime dateAttribution
        +Double tauxExecution
    }

    class LigneLot {
        +Long id
        +Long ligneBesoinId
        +String designation
        +Integer quantite
        +BigDecimal prixUnitaire
        +BigDecimal montantTotal
    }

    class Fournisseur {
        +Long id
        +String raisonSociale
        +String ninea
        +String adresse
        +String telephone
        +String email
        +String domaineIntervention
        +boolean actif
    }

    class OffreTarifaire {
        +Long id
        +Long lotId
        +Long fournisseurId
        +String fournisseurNom
        +StatutOffre statut
        +BigDecimal montantTotal
        +String devise
        +LocalDateTime dateOffre
    }

    class LigneOffre {
        +Long id
        +String designation
        +Integer quantite
        +BigDecimal prixUnitaire
        +BigDecimal montantLigne
    }

    class Livraison {
        +Long id
        +Long lotId
        +LocalDateTime dateLivraison
        +Integer quantiteLivree
        +String designation
        +Boolean conforme
    }

    class HistoriqueStatut {
        +Long id
        +String entiteType
        +Long entiteId
        +String ancienStatut
        +String nouveauStatut
        +LocalDateTime dateChangement
        +String commentaire
    }

    ExpressionBesoin "1" *-- "1..*" LigneBesoin : contient
    Marche "1" *-- "1..*" Lot : structuré en
    Lot "1" *-- "1..*" LigneLot : contient
    Lot "1" ..> "0..*" OffreTarifaire : reçoit
    OffreTarifaire "1" *-- "1..*" LigneOffre : détaille
    Lot "1" ..> "0..*" Livraison : suit
    Fournisseur "1" ..> "0..*" OffreTarifaire : soumet
```

---

## 3. Diagrammes de Séquence

### 3.1 Cycle d'une Expression de Besoin (Création -> Validation)

```mermaid
sequenceDiagram
    autonumber
    actor Chef as Chef de Service
    participant FE as Frontend Angular
    participant GW as API Gateway (8080)
    participant Auth as Auth Service (8081)
    participant Proc as Procurement Service (8082)
    actor Achats as Service des Achats

    Chef->>FE: Remplir formulaire d'expression de besoin
    FE->>GW: POST /api/besoins (avec Bearer JWT)
    GW->>Proc: Routage vers /api/besoins
    Proc->>Proc: Vérifier rôle (CHEF_SERVICE) & générer référence
    Proc->>Proc: Persister au statut EN_ATTENTE
    Proc-->>FE: 201 Created (BesoinDTO)
    FE-->>Chef: Notification de succès avec référence EB-...

    Achats->>FE: Consulter les demandes à traiter
    FE->>GW: GET /api/besoins?statut=EN_ATTENTE
    GW->>Proc: Routage vers /api/besoins
    Proc-->>FE: Liste des besoins EN_ATTENTE
    FE-->>Achats: Affichage du tableau de bord Achats

    Achats->>FE: Cliquer sur "Valider" avec commentaire
    FE->>GW: PUT /api/besoins/{id}/valider
    GW->>Proc: Routage vers validation
    Proc->>Proc: Contrôle statut initial & enregistrement historique
    Proc->>Proc: Mise à jour statut -> VALIDE
    Proc-->>FE: 200 OK (Besoin validé)
    FE-->>Achats: Confirmation de validation
```

### 3.2 Attribution d'un Lot et Enregistrement de Livraison

```mermaid
sequenceDiagram
    autonumber
    actor Achats as Service des Achats
    participant FE as Frontend Angular
    participant GW as API Gateway
    participant Proc as Procurement Service
    participant Supp as Supplier Service

    Achats->>FE: Consulter comparatif des offres du lot
    FE->>GW: GET /api/lots/{id}/offres/comparer
    GW->>Proc: Routage vers comparerOffres
    Proc-->>FE: Offres triées par prix croissant
    FE-->>Achats: Affichage du tableau comparatif

    Achats->>FE: Sélectionner l'offre retenue & attribuer le lot
    FE->>GW: PUT /api/lots/{id}/attribuer
    GW->>Proc: Routage vers attribution
    Proc->>Supp: GET /api/fournisseurs/{id}/actif
    Supp-->>Proc: true (fournisseur en règle)
    Proc->>Proc: Statut lot -> ATTRIBUE, offre -> RETENUE
    Proc->>Proc: Recalculer montant total du marché
    Proc-->>FE: 200 OK (Lot attribué)
    FE-->>Achats: Notification succès attribution

    Achats->>FE: Enregistrer réception matériels (quantité livrée)
    FE->>GW: POST /api/livraisons
    GW->>Proc: Routage livraison
    Proc->>Proc: Vérifier plafond quantitatif
    Proc->>Proc: Recalculer taux d'exécution (%) & statut lot
    Proc-->>FE: 201 Created (Taux mis à jour)
    FE-->>Achats: Affichage barre de progression actualisée
```

---

## 4. Diagramme de Déploiement et Composants

```mermaid
flowchart TD
    Client["Navigateur Web (Utilisateurs EPT)"]
    
    subgraph ReverseProxy["Couche d'Entrée"]
        GW["API Gateway (Port 8080)\nSpring Cloud Gateway"]
    end

    subgraph Microservices["Couche Métier Microservices (Spring Boot 3.2.5 / Java 21)"]
        AuthSvc["Auth Service (Port 8081)\nAuthentification & Sécurité"]
        ProcSvc["Procurement Service (Port 8082)\nBesoins, Marchés, Lots, Livraisons"]
        SuppSvc["Supplier Service (Port 8083)\nRéférentiel Fournisseurs"]
        DashSvc["Dashboard Service (Port 8084)\nIndicateurs & Exports"]
        BotSvc["Chatbot Service (Port 8085)\nMoteur NLP Local"]
    end

    subgraph Persistence["Couche Données Persistantes (MySQL 8.0)"]
        DB_Auth[("ept_auth\nComptes, Rôles, Services")]
        DB_Proc[("ept_procurement\nMarchés, Lots, Offres, Livraisons")]
        DB_Supp[("ept_supplier\nAnnuaire Fournisseurs")]
    end

    Client -- HTTP/REST :4200 --> GW
    GW --> AuthSvc
    GW --> ProcSvc
    GW --> SuppSvc
    GW --> DashSvc
    GW --> BotSvc

    AuthSvc --> DB_Auth
    ProcSvc --> DB_Proc
    SuppSvc --> DB_Supp

    ProcSvc -- Appel REST synchrone --> SuppSvc
    DashSvc -- Appel REST synchrone --> ProcSvc
    DashSvc -- Appel REST synchrone --> SuppSvc
    BotSvc -- Appel REST synchrone --> ProcSvc
    BotSvc -- Appel REST synchrone --> SuppSvc
```
