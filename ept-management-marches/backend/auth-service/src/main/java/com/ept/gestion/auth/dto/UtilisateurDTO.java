package com.ept.gestion.auth.dto;

import com.ept.gestion.common.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de sortie représentant un utilisateur (sans le mot de passe).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un utilisateur")
public class UtilisateurDTO {

    @Schema(description = "Identifiant unique", example = "1")
    private Long id;

    @Schema(description = "Nom de famille", example = "NIANG")
    private String nom;

    @Schema(description = "Prénom", example = "Nafissatou")
    private String prenom;

    @Schema(description = "Adresse e-mail", example = "nafissatou.niang@ept.sn")
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+221 77 123 45 67")
    private String telephone;

    @Schema(description = "Compte actif", example = "true")
    private boolean actif;

    @Schema(description = "Rôle de l'utilisateur", example = "CHEF_SERVICE")
    private RoleEnum role;

    @Schema(description = "Nom du service/département", example = "Informatique")
    private String serviceNom;

    @Schema(description = "Date de création du compte")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de la dernière connexion")
    private LocalDateTime dateDerniereConnexion;
}
