package com.ept.gestion.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un service/département de l'EPT.
 * Par exemple : Informatique, Génie Civil, Direction, etc.
 */
@Entity
@Table(name = "services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDepartement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom unique du service/département */
    @NotBlank(message = "Le nom du service est obligatoire")
    @Column(nullable = false, unique = true)
    private String nom;

    /** Description du service/département */
    @Column
    private String description;

    /** Indicateur d'activation du service */
    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;
}
