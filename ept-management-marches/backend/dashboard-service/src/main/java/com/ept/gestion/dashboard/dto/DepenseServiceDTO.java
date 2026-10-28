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
public class DepenseServiceDTO {
    private String serviceNom;
    private int nombreDemandes;
    private BigDecimal montantTotal;
}
