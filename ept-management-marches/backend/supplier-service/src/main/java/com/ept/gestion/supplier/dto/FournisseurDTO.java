package com.ept.gestion.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant un fournisseur.
 * <p>
 * Utilisé pour les réponses API afin de ne pas exposer
 * directement l'entité JPA.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurDTO {

    /** Identifiant unique du fournisseur */
    private Long id;

    /** Raison sociale (nom de l'entreprise) */
    private String raisonSociale;

    /** Numéro d'Identification Nationale des Entreprises et Associations */
    private String ninea;

    /** Adresse postale */
    private String adresse;

    /** Numéro de téléphone principal */
    private String telephone;

    /** Adresse email */
    private String email;

    /** Domaine d'intervention */
    private String domaineIntervention;

    /** Site web */
    private String siteWeb;

    /** Nom du contact principal */
    private String contactPrincipal;

    /** Statut d'activation */
    private boolean actif;

    /** Date de création de l'enregistrement */
    private LocalDateTime dateCreation;

    /** Date de dernière modification */
    private LocalDateTime dateModification;

    /** Observations ou notes complémentaires */
    private String observations;
}
