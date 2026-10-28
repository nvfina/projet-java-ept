# Guide des Tests Automatisés et Scénarios de Bout en Bout

## Système de Management et de Suivi des Marchés de l'EPT

---

## 1. Exécution des Tests Backend

Pour exécuter tous les tests automatisés JUnit 5 et Spring Boot Test :
```bash
mvn clean test
```

Pour exécuter les tests d'un service spécifique :
```bash
# Tests du service d'authentification
mvn test -pl backend/auth-service

# Tests du service fournisseurs
mvn test -pl backend/supplier-service

# Tests du service approvisionnement (procurement)
mvn test -pl backend/procurement-service
```

---

## 2. Scénarios Métier de Bout en Bout à Valider

Les 11 scénarios exigés par le cahier des charges sont entièrement couverts par l'application :

### Scénario 1 : Création d'une expression de besoin
- **Acteur** : Chef de Service Informatique (`chef.info@ept.sn`)
- **Action** : Se connecter, accéder à "Nouvelle Demande", saisir la description, le motif et ajouter 2 lignes d'articles avec quantités et estimations.
- **Résultat attendu** : Demande enregistrée avec le préfixe unique `EB-YYYYMMDD-XXX` et le statut `EN_ATTENTE`.

### Scénario 2 : Consultation de ses propres demandes
- **Acteur** : Chef de Service Informatique
- **Action** : Accéder à l'écran "Mes demandes".
- **Résultat attendu** : Seules les demandes émises par son service sont visibles. Impossibilité de voir celles des autres départements.

### Scénario 3 : Prise en charge par le Service des Achats
- **Acteur** : Responsable Achats (`achats@ept.sn`)
- **Action** : Consulter la liste globale des demandes avec filtre `EN_ATTENTE`.
- **Résultat attendu** : Visualisation exhaustive des demandes de tous les départements (Informatique, Génie Civil, etc.).

### Scénario 4 : Validation d'un besoin
- **Acteur** : Responsable Achats
- **Action** : Ouvrir la demande, cliquer sur "Valider" et saisir un commentaire décisionnel.
- **Résultat attendu** : Statut passe à `VALIDE`, date de validation et identité du validateur consignées dans l'historique d'audit.

### Scénario 5 : Montage d'un marché et découpage en lots
- **Acteur** : Responsable Achats
- **Action** : Créer un marché (référence auto `MRC-YYYY-XXX`), définir l'objet, budget prévisionnel, et rattacher les besoins validés sous forme de Lots 1 et 2.
- **Résultat attendu** : Marché et lots persistés avec cohérence des quantités et estimations.

### Scénario 6 : Enregistrement de prestataires et d'offres tarifaires
- **Acteur** : Responsable Achats
- **Action** : Créer les fournisseurs avec NINEA, puis enregistrer plusieurs offres de prix pour le Lot 1.
- **Résultat attendu** : Calcul automatique des totaux côté serveur, comparaison ordonnée par prix croissant.

### Scénario 7 : Sélection d'une offre et attribution du lot
- **Acteur** : Responsable Achats
- **Action** : Sélectionner l'offre retenue et attribuer le lot au prestataire.
- **Résultat attendu** : Le lot passe à `ATTRIBUE`, l'offre sélectionnée passe à `RETENUE`, le marché parent est mis à jour et son montant total recalculé.

### Scénario 8 : Enregistrement des livraisons et calcul du taux
- **Acteur** : Responsable Achats
- **Action** : Saisir une première livraison de 15 unités sur 25 attendues.
- **Résultat attendu** : Taux d'exécution calculé automatiquement à 60%, statut du lot passe à `LIVRE_PARTIELLEMENT`. Le dépassement de quantité est rejeté par le backend.

### Scénario 9 : Consultation des indicateurs par la Direction
- **Acteur** : Direction EPT (`direction@ept.sn`)
- **Action** : Accéder au Tableau de Bord décisionnel.
- **Résultat attendu** : Indicateurs calculés sur données réelles (taux global, total engagé, dépenses par service et marché). Exportation Excel et CSV fonctionnelle.

### Scénario 10 : Recherche en langage naturel avec le Chatbot
- **Acteur** : Utilisateur authentifié
- **Action** : Poser la question : *"Quels sont les marchés actuellement en cours ?"* ou *"Quels fournisseurs interviennent dans le domaine informatique ?"*.
- **Résultat attendu** : Réponse textuelle précise fondée sur les données MySQL effectives, avec suggestions interactives.

### Scénario 11 : Sécurité et contrôle d'accès
- **Acteur** : Utilisateur avec rôle Chef de Service
- **Action** : Tenter d'appeler directement l'API de validation `PUT /api/besoins/{id}/valider` ou de modification de marché.
- **Résultat attendu** : Code HTTP `403 Forbidden` retourné par Spring Security avec message d'erreur standardisé.
