package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutOffre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffreDTO {
    private Long id;
    private Long lotId;
    private String lotDesignation;
    private Long fournisseurId;
    private String fournisseurNom;
    private LocalDateTime dateOffre;
    private StatutOffre statut;
    private BigDecimal montantTotal;
    private String devise;
    private String observations;
    private LocalDateTime dateEvaluation;
    private Long evaluateurId;
    @Builder.Default
    private List<LigneOffreDTO> lignesOffre = new ArrayList<>();
}
