package com.ept.gestion.auth.repository;

import com.ept.gestion.auth.entity.Utilisateur;
import com.ept.gestion.common.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des utilisateurs.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email l'adresse e-mail
     * @return l'utilisateur trouvé (optionnel)
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Recherche tous les utilisateurs ayant un rôle donné.
     *
     * @param role le rôle recherché
     * @return la liste des utilisateurs correspondants
     */
    List<Utilisateur> findByRole(RoleEnum role);

    /**
     * Vérifie si un utilisateur existe avec l'adresse e-mail donnée.
     *
     * @param email l'adresse e-mail
     * @return true si un utilisateur existe avec cet e-mail
     */
    boolean existsByEmail(String email);

    /**
     * Recherche tous les utilisateurs appartenant à un service donné.
     *
     * @param serviceNom le nom du service
     * @return la liste des utilisateurs du service
     */
    List<Utilisateur> findByServiceNom(String serviceNom);

    /**
     * Liste paginée de tous les utilisateurs.
     *
     * @param pageable les paramètres de pagination
     * @return la page d'utilisateurs
     */
    Page<Utilisateur> findAll(Pageable pageable);
}
