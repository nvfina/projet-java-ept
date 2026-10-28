package com.ept.gestion.supplier.repository;

import com.ept.gestion.supplier.entity.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour l'entité Fournisseur.
 * <p>
 * Fournit les opérations CRUD de base ainsi que des requêtes
 * personnalisées pour la recherche de fournisseurs.
 * Implémente JpaSpecificationExecutor pour la recherche multicritères dynamique.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long>, JpaSpecificationExecutor<Fournisseur> {

    /**
     * Recherche des fournisseurs par raison sociale (recherche partielle, insensible à la casse)
     *
     * @param raisonSociale le terme de recherche
     * @param pageable      les paramètres de pagination
     * @return une page de fournisseurs correspondants
     */
    Page<Fournisseur> findByRaisonSocialeContainingIgnoreCase(String raisonSociale, Pageable pageable);

    /**
     * Recherche un fournisseur par son NINEA exact
     *
     * @param ninea le NINEA à rechercher
     * @return le fournisseur correspondant, s'il existe
     */
    Optional<Fournisseur> findByNinea(String ninea);

    /**
     * Recherche des fournisseurs par domaine d'intervention (recherche partielle, insensible à la casse)
     *
     * @param domaineIntervention le domaine à rechercher
     * @return la liste des fournisseurs correspondants
     */
    List<Fournisseur> findByDomaineInterventionContainingIgnoreCase(String domaineIntervention);

    /**
     * Recherche des fournisseurs par statut d'activation
     *
     * @param actif le statut recherché
     * @return la liste des fournisseurs correspondants
     */
    List<Fournisseur> findByActif(boolean actif);

    /**
     * Recherche des fournisseurs par statut d'activation avec pagination
     *
     * @param actif    le statut recherché
     * @param pageable les paramètres de pagination
     * @return une page de fournisseurs correspondants
     */
    Page<Fournisseur> findByActif(boolean actif, Pageable pageable);

    /**
     * Vérifie si un fournisseur avec le NINEA donné existe
     *
     * @param ninea le NINEA à vérifier
     * @return true si un fournisseur avec ce NINEA existe
     */
    boolean existsByNinea(String ninea);

    /**
     * Vérifie si un fournisseur avec la raison sociale donnée existe
     *
     * @param raisonSociale la raison sociale à vérifier
     * @return true si un fournisseur avec cette raison sociale existe
     */
    boolean existsByRaisonSociale(String raisonSociale);

    /**
     * Vérifie si un autre fournisseur (différent de l'ID donné) utilise déjà cette raison sociale
     *
     * @param raisonSociale la raison sociale à vérifier
     * @param id            l'ID du fournisseur à exclure
     * @return true si un doublon existe
     */
    boolean existsByRaisonSocialeAndIdNot(String raisonSociale, Long id);

    /**
     * Recherche combinée par raison sociale et/ou domaine d'intervention avec pagination
     *
     * @param raisonSociale       le terme de recherche pour la raison sociale (peut être null)
     * @param domaineIntervention le terme de recherche pour le domaine (peut être null)
     * @param pageable            les paramètres de pagination
     * @return une page de fournisseurs correspondants
     */
    @Query("SELECT f FROM Fournisseur f WHERE " +
            "(:raisonSociale IS NULL OR LOWER(f.raisonSociale) LIKE LOWER(CONCAT('%', :raisonSociale, '%'))) AND " +
            "(:domaineIntervention IS NULL OR LOWER(f.domaineIntervention) LIKE LOWER(CONCAT('%', :domaineIntervention, '%')))")
    Page<Fournisseur> rechercherParCriteres(
            @Param("raisonSociale") String raisonSociale,
            @Param("domaineIntervention") String domaineIntervention,
            Pageable pageable
    );
}
