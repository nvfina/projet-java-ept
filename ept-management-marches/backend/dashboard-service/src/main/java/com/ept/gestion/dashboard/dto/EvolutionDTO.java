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
public class EvolutionDTO {
    private String periode;
    private long nombreDemandes;
    private BigDecimal montantDepenses;
}
