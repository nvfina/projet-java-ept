package com.ept.gestion.procurement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité traçant l'historique des changements de statut
 * sur les besoins, marchés et lots.
 */
@Entity
@Table(name = "historique_statuts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Type d'entité concernée : BESOIN, MARCHE, LOT */
    @Column(name = "entite_type", nullable = false)
    private String entiteType;

    /** Identifiant de l'entité concernée */
    @Column(name = "entite_id", nullable = false)
    private Long entiteId;

    /** Ancien statut avant le changement */
    @Column(name = "ancien_statut")
    private String ancienStatut;

    /** Nouveau statut après le changement */
    @Column(name = "nouveau_statut", nullable = false)
    private String nouveauStatut;

    /** Date et heure du changement */
    @CreationTimestamp
    @Column(name = "date_changement", updatable = false)
    private LocalDateTime dateChangement;

    /** Identifiant de l'utilisateur ayant effectué le changement */
    @Column(name = "utilisateur_id")
    private Long utilisateurId;

    /** Nom de l'utilisateur ayant effectué le changement */
    @Column(name = "utilisateur_nom")
    private String utilisateurNom;

    /** Commentaire accompagnant le changement */
    @Column(columnDefinition = "TEXT")
    private String commentaire;
}
