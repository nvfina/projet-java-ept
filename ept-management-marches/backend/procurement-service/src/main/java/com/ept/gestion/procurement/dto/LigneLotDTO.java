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
public class LigneLotDTO {
    private Long id;
    private Long ligneBesoinId;
    private String designation;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
}
