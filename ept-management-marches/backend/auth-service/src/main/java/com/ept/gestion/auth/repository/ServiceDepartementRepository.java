package com.ept.gestion.auth.repository;

import com.ept.gestion.auth.entity.ServiceDepartement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'accès aux données des services/départements.
 */
@Repository
public interface ServiceDepartementRepository extends JpaRepository<ServiceDepartement, Long> {

    /**
     * Recherche un service par son nom.
     *
     * @param nom le nom du service
     * @return le service trouvé (optionnel)
     */
    Optional<ServiceDepartement> findByNom(String nom);

    /**
     * Vérifie si un service existe avec le nom donné.
     *
     * @param nom le nom du service
     * @return true si un service existe avec ce nom
     */
    boolean existsByNom(String nom);
}
