package com.ept.gestion.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopFournisseurDTO {
    private String fournisseurNom;
    private int nombreLots;
    private BigDecimal montantTotal;
}
