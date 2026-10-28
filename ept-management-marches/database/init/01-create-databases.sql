-- ============================================================
-- Script d'initialisation des bases de données EPT
-- Ce script est exécuté automatiquement par Docker au démarrage
-- ============================================================

-- Base de données pour le service d'authentification
CREATE DATABASE IF NOT EXISTS ept_auth
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Base de données pour le service d'approvisionnement
CREATE DATABASE IF NOT EXISTS ept_procurement
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Base de données pour le service des fournisseurs
CREATE DATABASE IF NOT EXISTS ept_supplier
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Utilisateur applicatif (optionnel, pour ne pas utiliser root en production)
-- CREATE USER IF NOT EXISTS 'ept_app'@'%' IDENTIFIED BY 'ept_app_password';
-- GRANT ALL PRIVILEGES ON ept_auth.* TO 'ept_app'@'%';
-- GRANT ALL PRIVILEGES ON ept_procurement.* TO 'ept_app'@'%';
-- GRANT ALL PRIVILEGES ON ept_supplier.* TO 'ept_app'@'%';
-- FLUSH PRIVILEGES;
