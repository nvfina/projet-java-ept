package com.ept.gestion.procurement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant une livraison effectuée pour un lot.
 * Permet de suivre les livraisons partielles et complètes.
 */
@Entity
@Table(name = "livraisons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du lot concerné */
    @Column(name = "lot_id", nullable = false)
    private Long lotId;

    /** Date de livraison */
    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;

    /** Quantité livrée */
    @Column(name = "quantite_livree", nullable = false)
    private Integer quantiteLivree;

    /** Désignation des articles livrés */
    @Column
    private String designation;

    /** Observations sur la livraison */
    @Column(columnDefinition = "TEXT")
    private String observations;

    /** Identifiant du réceptionnaire */
    @Column(name = "receptionnaire_id")
    private Long receptionnaireId;

    /** Nom du réceptionnaire */
    @Column(name = "receptionnaire_nom")
    private String receptionnaireNom;

    /** Indique si la livraison est conforme */
    @Column(nullable = false)
    @Builder.Default
    private Boolean conforme = true;
}
