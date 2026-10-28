package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutMarche;
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
public class MarcheUpdateRequest {
    private String objet;
    private String description;
    private BigDecimal budgetPrevisionnel;
    private LocalDateTime dateLancement;
    private LocalDateTime dateCloture;
    private StatutMarche statut;
    private String observations;
}
