package com.ept.gestion.procurement.entity;

import com.ept.gestion.common.enums.StatutBesoin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une expression de besoin.
 * Une expression de besoin est émise par un chef de service
 * et contient une ou plusieurs lignes de besoin.
 */
@Entity
@Table(name = "expressions_besoin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "lignes")
@EqualsAndHashCode(exclude = "lignes")
public class ExpressionBesoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Référence unique auto-générée au format "EB-YYYYMMDD-XXX" */
    @Column(unique = true, nullable = false)
    private String reference;

    /** Service à l'origine de la demande */
    @Column(name = "service_demandeur", nullable = false)
    private String serviceDemandeur;

    /** Identifiant de l'utilisateur demandeur dans auth-service */
    @Column(name = "demandeur_id", nullable = false)
    private Long demandeurId;

    /** Nom complet du demandeur */
    @Column(name = "demandeur_nom")
    private String demandeurNom;

    /** Description détaillée du besoin */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Motif justifiant le besoin */
    @Column(columnDefinition = "TEXT")
    private String motif;

    /** Statut courant du besoin */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutBesoin statut = StatutBesoin.EN_ATTENTE;

    /** Date de création automatique */
    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /** Date de dernière modification automatique */
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    /** Date de validation ou de rejet */
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    /** Identifiant du validateur */
    @Column(name = "validateur_id")
    private Long validateurId;

    /** Nom du validateur */
    @Column(name = "validateur_nom")
    private String validateurNom;

    /** Commentaire accompagnant la décision de validation/rejet */
    @Column(name = "commentaire_decision", columnDefinition = "TEXT")
    private String commentaireDecision;

    /** Lignes composant l'expression de besoin */
    @OneToMany(mappedBy = "expressionBesoin", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneBesoin> lignes = new ArrayList<>();

    /** Indique si le besoin est urgent */
    @Column(nullable = false)
    @Builder.Default
    private boolean urgente = false;

    /**
     * Ajoute une ligne de besoin et établit la relation bidirectionnelle.
     */
    public void addLigne(LigneBesoin ligne) {
        lignes.add(ligne);
        ligne.setExpressionBesoin(this);
    }

    /**
     * Supprime une ligne de besoin et casse la relation bidirectionnelle.
     */
    public void removeLigne(LigneBesoin ligne) {
        lignes.remove(ligne);
        ligne.setExpressionBesoin(null);
    }
}
