package com.ept.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse de connexion contenant le token JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse de connexion avec token JWT")
public class LoginResponse {

    @Schema(description = "Token JWT d'authentification")
    private String token;

    @Builder.Default
    @Schema(description = "Type du token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Adresse e-mail de l'utilisateur", example = "admin@ept.sn")
    private String email;

    @Schema(description = "Nom de l'utilisateur", example = "NIANG")
    private String nom;

    @Schema(description = "Prénom de l'utilisateur", example = "Nafissatou")
    private String prenom;

    @Schema(description = "Rôle de l'utilisateur", example = "ADMIN")
    private String role;

    @Schema(description = "Service/département de l'utilisateur", example = "Informatique")
    private String service;

    @Schema(description = "Durée de validité du token en millisecondes", example = "86400000")
    private Long expiresIn;
}
