package com.ept.gestion.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributionRequest {
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;
    private String fournisseurNom;
    private Long offreId;
    @NotNull(message = "Le montant d'attribution est obligatoire")
    private BigDecimal montantAttribue;
    private LocalDateTime dateLivraisonPrevue;
    private String observations;
}
