package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutLot;
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
public class LotDTO {
    private Long id;
    private Integer numero;
    private String designation;
    private String description;
    private Long marcheId;
    private String marcheReference;
    private StatutLot statut;
    private BigDecimal montantEstime;
    private BigDecimal montantAttribue;
    private Long fournisseurId;
    private String fournisseurNom;
    private LocalDateTime dateAttribution;
    private LocalDateTime dateLivraisonPrevue;
    private LocalDateTime dateLivraisonEffective;
    private Double tauxExecution;
    private String observations;
    @Builder.Default
    private List<LigneLotDTO> lignesLot = new ArrayList<>();
}
