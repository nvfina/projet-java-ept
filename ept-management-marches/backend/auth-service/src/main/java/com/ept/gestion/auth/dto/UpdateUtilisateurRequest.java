package com.ept.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de mise à jour d'un utilisateur.
 * Les champs sont optionnels : seuls les champs renseignés seront mis à jour.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de mise à jour d'un utilisateur")
public class UpdateUtilisateurRequest {

    @Schema(description = "Nouveau nom de famille", example = "DIOP")
    private String nom;

    @Schema(description = "Nouveau prénom", example = "Aminata")
    private String prenom;

    @Schema(description = "Nouveau numéro de téléphone", example = "+221 78 987 65 43")
    private String telephone;

    @Schema(description = "Nouveau service/département de rattachement", example = "Génie Civil")
    private String serviceNom;
}
