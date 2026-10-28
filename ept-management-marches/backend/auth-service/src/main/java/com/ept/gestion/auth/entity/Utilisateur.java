package com.ept.gestion.auth.entity;

import com.ept.gestion.common.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un utilisateur du système.
 * Contient les informations d'identification, le rôle et le service d'appartenance.
 */
@Entity
@Table(name = "utilisateurs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de famille de l'utilisateur */
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    /** Prénom de l'utilisateur */
    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    /** Adresse e-mail unique servant d'identifiant de connexion */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(nullable = false, unique = true)
    private String email;

    /** Mot de passe haché en BCrypt */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    /** Numéro de téléphone (optionnel) */
    @Column
    private String telephone;

    /** Indicateur d'activation du compte */
    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;

    /** Rôle de l'utilisateur dans le système */
    @NotNull(message = "Le rôle est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    /** Nom du service/département de rattachement */
    @Column(name = "service_nom")
    private String serviceNom;

    /** Date de création du compte */
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /** Date de la dernière connexion */
    @Column(name = "date_derniere_connexion")
    private LocalDateTime dateDerniereConnexion;

    /**
     * Initialise automatiquement la date de création avant la persistance.
     */
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}
