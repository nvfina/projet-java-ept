package com.ept.gestion.procurement.dto;

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
public class LotUpdateRequest {
    private String designation;
    private String description;
    private BigDecimal montantEstime;
    private LocalDateTime dateLivraisonPrevue;
    private String observations;
}
