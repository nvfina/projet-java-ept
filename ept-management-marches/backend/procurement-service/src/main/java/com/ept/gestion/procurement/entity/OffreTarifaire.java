package com.ept.gestion.procurement.entity;

import com.ept.gestion.common.enums.StatutOffre;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une offre tarifaire soumise par un fournisseur
 * pour un lot donné.
 */
@Entity
@Table(name = "offres_tarifaires")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "lignesOffre")
@EqualsAndHashCode(exclude = "lignesOffre")
public class OffreTarifaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du lot concerné */
    @Column(name = "lot_id", nullable = false)
    private Long lotId;

    /** Identifiant du fournisseur dans supplier-service */
    @Column(name = "fournisseur_id", nullable = false)
    private Long fournisseurId;

    /** Nom du fournisseur */
    @Column(name = "fournisseur_nom")
    private String fournisseurNom;

    /** Date de soumission de l'offre */
    @Column(name = "date_offre")
    private LocalDateTime dateOffre;

    /** Statut courant de l'offre */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutOffre statut = StatutOffre.SOUMISE;

    /** Montant total de l'offre (calculé côté backend) */
    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;

    /** Devise de l'offre */
    @Column(nullable = false)
    @Builder.Default
    private String devise = "XOF";

    /** Observations complémentaires */
    @Column(columnDefinition = "TEXT")
    private String observations;

    /** Date d'évaluation */
    @Column(name = "date_evaluation")
    private LocalDateTime dateEvaluation;

    /** Identifiant de l'évaluateur */
    @Column(name = "evaluateur_id")
    private Long evaluateurId;

    /** Lignes composant l'offre */
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneOffre> lignesOffre = new ArrayList<>();

    /**
     * Ajoute une ligne à l'offre et établit la relation bidirectionnelle.
     */
    public void addLigneOffre(LigneOffre ligne) {
        lignesOffre.add(ligne);
        ligne.setOffre(this);
    }

    /**
     * Calcule le montant total de l'offre à partir de ses lignes.
     */
    public void calculerMontantTotal() {
        this.montantTotal = lignesOffre.stream()
                .map(LigneOffre::getMontantLigne)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
