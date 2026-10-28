package com.ept.gestion.procurement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entité représentant une ligne de besoin dans une expression de besoin.
 * Chaque ligne correspond à un article ou service demandé.
 */
@Entity
@Table(name = "lignes_besoin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "expressionBesoin")
@EqualsAndHashCode(exclude = "expressionBesoin")
public class LigneBesoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Expression de besoin parente */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expression_besoin_id", nullable = false)
    private ExpressionBesoin expressionBesoin;

    /** Désignation de l'article ou du service demandé */
    @Column(nullable = false)
    private String designation;

    /** Description complémentaire */
    @Column
    private String description;

    /** Quantité demandée (minimum 1) */
    @Column(name = "quantite_demandee", nullable = false)
    private Integer quantiteDemandee;

    /** Unité de mesure */
    @Column(nullable = false)
    @Builder.Default
    private String unite = "unité";

    /** Prix unitaire estimé */
    @Column(name = "estimation_prix", precision = 15, scale = 2)
    private BigDecimal estimationPrix;

    /** Observations éventuelles */
    @Column
    private String observations;
}
