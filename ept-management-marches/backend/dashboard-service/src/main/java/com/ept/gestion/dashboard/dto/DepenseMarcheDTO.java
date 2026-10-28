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
public class DepenseMarcheDTO {
    private String marcheReference;
    private String marcheObjet;
    private String statut;
    private BigDecimal budgetPrevisionnel;
    private BigDecimal montantTotal;
}
