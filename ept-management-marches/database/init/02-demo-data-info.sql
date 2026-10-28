-- ============================================================
-- Données de démonstration pour le service d'authentification
-- À exécuter APRÈS le démarrage du service auth (tables créées par JPA)
-- ============================================================

USE ept_auth;

-- Les données de démonstration sont insérées par le DataInitializer
-- du service auth-service au premier démarrage.
-- 
-- Comptes de démonstration créés automatiquement :
--
-- | Rôle           | Identifiant        | Mot de passe  |
-- |----------------|--------------------|---------------|
-- | ADMIN          | admin@ept.sn       | Admin@2024    |
-- | CHEF_SERVICE   | chef.info@ept.sn   | Chef@2024     |
-- | CHEF_SERVICE   | chef.civil@ept.sn  | Chef@2024     |
-- | SERVICE_ACHATS | achats@ept.sn      | Achats@2024   |
-- | DIRECTION      | direction@ept.sn   | Direction@2024|
--
-- Ces comptes sont créés uniquement si la table utilisateur est vide.
