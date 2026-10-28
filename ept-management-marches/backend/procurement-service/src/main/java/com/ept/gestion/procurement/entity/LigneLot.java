package com.ept.gestion.procurement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entité représentant une ligne de lot.
 * Chaque ligne fait référence à une ligne de besoin et
 * détaille la quantité et le prix unitaire retenus.
 */
@Entity
@Table(name = "lignes_lot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "lot")
@EqualsAndHashCode(exclude = "lot")
public class LigneLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lot auquel appartient cette ligne */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    /** Référence vers la ligne de besoin originale */
    @Column(name = "ligne_besoin_id")
    private Long ligneBesoinId;

    /** Désignation de l'article ou du service */
    @Column(nullable = false)
    private String designation;

    /** Quantité retenue */
    @Column(nullable = false)
    private Integer quantite;

    /** Prix unitaire */
    @Column(name = "prix_unitaire", precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    /** Montant total calculé (quantité × prix unitaire) */
    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;

    /**
     * Calcule et met à jour le montant total de la ligne.
     */
    @PrePersist
    @PreUpdate
    public void calculerMontantTotal() {
        if (quantite != null && prixUnitaire != null) {
            this.montantTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        }
    }
}
