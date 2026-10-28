package com.ept.gestion.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneOffreDTO {
    private Long id;
    private String designation;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantLigne;
    private String observations;
}
