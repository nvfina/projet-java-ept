package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutMarche;
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
public class MarcheDTO {
    private Long id;
    private String reference;
    private String objet;
    private String description;
    private StatutMarche statut;
    private BigDecimal budgetPrevisionnel;
    private BigDecimal montantTotal;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLancement;
    private LocalDateTime dateAttribution;
    private LocalDateTime dateCloture;
    private Long createurId;
    private String createurNom;
    private String observations;
    @Builder.Default
    private List<LotDTO> lots = new ArrayList<>();
}
