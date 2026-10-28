package com.ept.gestion.procurement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneOffreRequest {
    @NotBlank(message = "La désignation est obligatoire")
    private String designation;
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private Integer quantite;
    @NotNull(message = "Le prix unitaire est obligatoire")
    private BigDecimal prixUnitaire;
    private String observations;
}
