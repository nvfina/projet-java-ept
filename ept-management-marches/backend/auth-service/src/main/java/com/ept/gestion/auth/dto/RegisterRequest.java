package com.ept.gestion.auth.dto;

import com.ept.gestion.common.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de création d'un nouvel utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'inscription d'un nouvel utilisateur")
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom de famille", example = "NIANG")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Schema(description = "Prénom", example = "Nafissatou")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Schema(description = "Adresse e-mail unique", example = "nafissatou.niang@ept.sn")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Schema(description = "Mot de passe (min. 8 caractères)", example = "MonMotDePasse@2024")
    private String motDePasse;

    @Schema(description = "Numéro de téléphone", example = "+221 77 123 45 67")
    private String telephone;

    @NotNull(message = "Le rôle est obligatoire")
    @Schema(description = "Rôle de l'utilisateur", example = "CHEF_SERVICE")
    private RoleEnum role;

    @Schema(description = "Nom du service/département de rattachement", example = "Informatique")
    private String serviceNom;
}
