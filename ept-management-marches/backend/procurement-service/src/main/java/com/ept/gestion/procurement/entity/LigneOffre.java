package com.ept.gestion.procurement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entité représentant une ligne d'offre tarifaire.
 * Chaque ligne détaille un article/service avec sa quantité et son prix.
 */
@Entity
@Table(name = "lignes_offre")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "offre")
@EqualsAndHashCode(exclude = "offre")
public class LigneOffre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Offre tarifaire parente */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreTarifaire offre;

    /** Désignation de l'article ou du service */
    @Column(nullable = false)
    private String designation;

    /** Quantité proposée */
    @Column(nullable = false)
    private Integer quantite;

    /** Prix unitaire proposé */
    @Column(name = "prix_unitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    /** Montant de la ligne (calculé côté backend) */
    @Column(name = "montant_ligne", precision = 15, scale = 2)
    private BigDecimal montantLigne;

    /** Observations éventuelles */
    @Column
    private String observations;

    /**
     * Calcule et met à jour le montant de la ligne.
     */
    @PrePersist
    @PreUpdate
    public void calculerMontantLigne() {
        if (quantite != null && prixUnitaire != null) {
            this.montantLigne = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        }
    }
}
