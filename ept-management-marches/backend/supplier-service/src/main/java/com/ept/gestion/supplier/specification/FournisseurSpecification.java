package com.ept.gestion.supplier.specification;

import com.ept.gestion.supplier.dto.FournisseurSearchCriteria;
import com.ept.gestion.supplier.entity.Fournisseur;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Spécifications JPA pour la recherche multicritères dynamique des fournisseurs.
 * <p>
 * Utilise l'API Criteria de JPA pour construire des requêtes dynamiques
 * en combinant les critères de recherche fournis.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
public final class FournisseurSpecification {

    private FournisseurSpecification() {
        // Classe utilitaire - pas d'instanciation
    }

    /**
     * Construit une Specification JPA à partir des critères de recherche.
     * <p>
     * Les critères non-null sont combinés avec un ET logique.
     * Les recherches textuelles sont partielles et insensibles à la casse.
     * </p>
     *
     * @param criteria les critères de recherche
     * @return la Specification correspondante
     */
    public static Specification<Fournisseur> parCriteres(FournisseurSearchCriteria criteria) {
        return avecCriteres(criteria);
    }

    public static Specification<Fournisseur> avecCriteres(FournisseurSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre par raison sociale (recherche partielle, insensible à la casse)
            if (criteria.getRaisonSociale() != null && !criteria.getRaisonSociale().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("raisonSociale")),
                        "%" + criteria.getRaisonSociale().toLowerCase() + "%"
                ));
            }

            // Filtre par NINEA (recherche partielle, insensible à la casse)
            if (criteria.getNinea() != null && !criteria.getNinea().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ninea")),
                        "%" + criteria.getNinea().toLowerCase() + "%"
                ));
            }

            // Filtre par domaine d'intervention (recherche partielle, insensible à la casse)
            if (criteria.getDomaineIntervention() != null && !criteria.getDomaineIntervention().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("domaineIntervention")),
                        "%" + criteria.getDomaineIntervention().toLowerCase() + "%"
                ));
            }

            // Filtre par statut d'activation
            if (criteria.getActif() != null) {
                predicates.add(criteriaBuilder.equal(root.get("actif"), criteria.getActif()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
