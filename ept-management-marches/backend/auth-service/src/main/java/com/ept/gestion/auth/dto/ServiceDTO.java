package com.ept.gestion.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un service/département de l'EPT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un service/département")
public class ServiceDTO {

    @Schema(description = "Identifiant unique", example = "1")
    private Long id;

    @Schema(description = "Nom du service", example = "Informatique")
    private String nom;

    @Schema(description = "Description du service", example = "Département d'informatique de l'EPT")
    private String description;

    @Schema(description = "Service actif", example = "true")
    private boolean actif;
}
