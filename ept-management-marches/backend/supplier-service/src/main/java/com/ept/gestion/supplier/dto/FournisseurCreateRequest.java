package com.ept.gestion.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour la création d'un nouveau fournisseur.
 * <p>
 * Contient toutes les informations nécessaires à l'enregistrement
 * d'un nouveau fournisseur dans le système.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurCreateRequest {

    /**
     * Raison sociale du fournisseur (obligatoire, unique)
     */
    @NotBlank(message = "La raison sociale est obligatoire")
    private String raisonSociale;

    /**
     * Numéro d'Identification Nationale des Entreprises et Associations (unique)
     */
    private String ninea;

    /**
     * Adresse postale du fournisseur
     */
    private String adresse;

    /**
     * Numéro de téléphone principal (obligatoire)
     */
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    /**
     * Adresse email (doit être au format email valide)
     */
    @Email(message = "L'adresse email doit être valide")
    private String email;

    /**
     * Domaine d'intervention (obligatoire) - ex: Informatique, BTP, Fournitures de bureau
     */
    @NotBlank(message = "Le domaine d'intervention est obligatoire")
    private String domaineIntervention;

    /**
     * Site web du fournisseur
     */
    private String siteWeb;

    /**
     * Nom de la personne de contact principale
     */
    private String contactPrincipal;

    /**
     * Observations ou notes complémentaires
     */
    private String observations;
}
