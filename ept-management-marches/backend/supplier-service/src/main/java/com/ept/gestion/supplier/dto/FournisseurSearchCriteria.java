package com.ept.gestion.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO contenant les critères de recherche pour les fournisseurs.
 * <p>
 * Permet une recherche multicritères dynamique.
 * Tous les champs sont optionnels et combinés avec un ET logique.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurSearchCriteria {

    /** Filtre sur la raison sociale (recherche partielle, insensible à la casse) */
    private String raisonSociale;

    /** Filtre sur le NINEA (recherche exacte ou partielle) */
    private String ninea;

    /** Filtre sur le domaine d'intervention (recherche partielle, insensible à la casse) */
    private String domaineIntervention;

    /** Filtre sur le statut d'activation (null = tous) */
    private Boolean actif;
}
