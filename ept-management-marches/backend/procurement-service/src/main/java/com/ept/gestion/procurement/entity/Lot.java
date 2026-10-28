package com.ept.gestion.procurement.entity;

import com.ept.gestion.common.enums.StatutLot;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un lot au sein d'un marché.
 * Chaque lot peut être attribué à un fournisseur et suivi
 * en termes de livraison.
 */
@Entity
@Table(name = "lots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"marche", "lignesLot"})
@EqualsAndHashCode(exclude = {"marche", "lignesLot"})
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro du lot dans le marché */
    @Column(nullable = false)
    private Integer numero;

    /** Désignation du lot */
    @Column(nullable = false)
    private String designation;

    /** Description détaillée du lot */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Marché auquel appartient ce lot */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id", nullable = false)
    private Marche marche;

    /** Statut courant du lot */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutLot statut = StatutLot.EN_ATTENTE;

    /** Montant estimé du lot */
    @Column(name = "montant_estime", precision = 15, scale = 2)
    private BigDecimal montantEstime;

    /** Montant effectivement attribué */
    @Column(name = "montant_attribue", precision = 15, scale = 2)
    private BigDecimal montantAttribue;

    /** Identifiant du fournisseur attributaire dans supplier-service */
    @Column(name = "fournisseur_id")
    private Long fournisseurId;

    /** Nom du fournisseur attributaire */
    @Column(name = "fournisseur_nom")
    private String fournisseurNom;

    /** Date d'attribution du lot */
    @Column(name = "date_attribution")
    private LocalDateTime dateAttribution;

    /** Date de livraison prévue */
    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    /** Date de livraison effective */
    @Column(name = "date_livraison_effective")
    private LocalDateTime dateLivraisonEffective;

    /** Taux d'exécution du lot (0.0 à 100.0) */
    @Column(name = "taux_execution")
    @Builder.Default
    private Double tauxExecution = 0.0;

    /** Observations complémentaires */
    @Column(columnDefinition = "TEXT")
    private String observations;

    /** Lignes composant le lot */
    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneLot> lignesLot = new ArrayList<>();

    /**
     * Ajoute une ligne au lot.
     */
    public void addLigneLot(LigneLot ligne) {
        lignesLot.add(ligne);
        ligne.setLot(this);
    }
}
