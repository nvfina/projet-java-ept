package com.ept.gestion.supplier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant un fournisseur dans le système EPT.
 * <p>
 * Un fournisseur est une entreprise ou un prestataire référencé
 * dans le système pour l'attribution des marchés publics.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@Entity
@Table(name = "fournisseurs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fournisseur_ninea", columnNames = "ninea"),
        @UniqueConstraint(name = "uk_fournisseur_raison_sociale", columnNames = "raison_sociale")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Raison sociale du fournisseur (nom de l'entreprise)
     */
    @NotBlank(message = "La raison sociale est obligatoire")
    @Column(name = "raison_sociale", nullable = false, unique = true)
    private String raisonSociale;

    /**
     * Numéro d'Identification Nationale des Entreprises et Associations
     */
    @Column(name = "ninea", unique = true)
    private String ninea;

    /**
     * Adresse postale du fournisseur
     */
    @Column(name = "adresse")
    private String adresse;

    /**
     * Numéro de téléphone principal
     */
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Column(name = "telephone", nullable = false)
    private String telephone;

    /**
     * Adresse email du fournisseur
     */
    @Email(message = "L'adresse email doit être valide")
    @Column(name = "email")
    private String email;

    /**
     * Domaine d'intervention du fournisseur (ex: Informatique, BTP, Fournitures de bureau)
     */
    @NotBlank(message = "Le domaine d'intervention est obligatoire")
    @Column(name = "domaine_intervention", nullable = false)
    private String domaineIntervention;

    /**
     * Site web du fournisseur
     */
    @Column(name = "site_web")
    private String siteWeb;

    /**
     * Nom de la personne de contact principale
     */
    @Column(name = "contact_principal")
    private String contactPrincipal;

    /**
     * Statut d'activation du fournisseur.
     * Un fournisseur désactivé n'est plus sélectionnable pour de nouvelles attributions.
     */
    @Builder.Default
    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    /**
     * Date de création de l'enregistrement
     */
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Date de dernière modification
     */
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    /**
     * Observations ou notes complémentaires sur le fournisseur
     */
    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;
}
