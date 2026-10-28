package com.ept.gestion.supplier.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requête pour la modification d'un fournisseur existant.
 * <p>
 * Tous les champs sont optionnels ; seuls les champs renseignés
 * seront mis à jour. Le NINEA n'est pas modifiable après création.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurUpdateRequest {

    /**
     * Nouvelle raison sociale (vérifie l'unicité si modifiée)
     */
    private String raisonSociale;

    /**
     * Nouvelle adresse postale
     */
    private String adresse;

    /**
     * Nouveau numéro de téléphone
     */
    private String telephone;

    /**
     * Nouvelle adresse email
     */
    @Email(message = "L'adresse email doit être valide")
    private String email;

    /**
     * Nouveau domaine d'intervention
     */
    private String domaineIntervention;

    /**
     * Nouveau site web
     */
    private String siteWeb;

    /**
     * Nouveau nom du contact principal
     */
    private String contactPrincipal;

    /**
     * Nouvelles observations
     */
    private String observations;
}
