package com.ept.gestion.procurement.entity;

import com.ept.gestion.common.enums.StatutMarche;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un marché public.
 * Un marché contient un ou plusieurs lots et suit un cycle de vie
 * depuis le brouillon jusqu'à la clôture.
 */
@Entity
@Table(name = "marches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "lots")
@EqualsAndHashCode(exclude = "lots")
public class Marche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence unique auto-générée au format "MRC-YYYY-XXX" */
    @Column(unique = true, nullable = false)
    private String reference;

    /** Objet du marché */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String objet;

    /** Description détaillée du marché */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Statut courant du marché */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutMarche statut = StatutMarche.BROUILLON;

    /** Budget prévisionnel du marché */
    @Column(name = "budget_previsionnel", precision = 15, scale = 2)
    private BigDecimal budgetPrevisionnel;

    /** Montant total calculé (somme des lots attribués) */
    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;

    /** Date de création */
    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /** Date de lancement du marché */
    @Column(name = "date_lancement")
    private LocalDateTime dateLancement;

    /** Date d'attribution */
    @Column(name = "date_attribution")
    private LocalDateTime dateAttribution;

    /** Date de clôture */
    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    /** Identifiant du créateur */
    @Column(name = "createur_id")
    private Long createurId;

    /** Nom du créateur */
    @Column(name = "createur_nom")
    private String createurNom;

    /** Observations complémentaires */
    @Column(columnDefinition = "TEXT")
    private String observations;

    /** Lots composant le marché */
    @OneToMany(mappedBy = "marche", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lot> lots = new ArrayList<>();

    /**
     * Ajoute un lot au marché.
     */
    public void addLot(Lot lot) {
        lots.add(lot);
        lot.setMarche(this);
    }

    /**
     * Supprime un lot du marché.
     */
    public void removeLot(Lot lot) {
        lots.remove(lot);
        lot.setMarche(null);
    }
}
