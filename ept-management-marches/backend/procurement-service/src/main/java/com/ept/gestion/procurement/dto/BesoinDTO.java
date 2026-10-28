package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutBesoin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BesoinDTO {
    private Long id;
    private String reference;
    private String serviceDemandeur;
    private Long demandeurId;
    private String demandeurNom;
    private String description;
    private String motif;
    private StatutBesoin statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateValidation;
    private Long validateurId;
    private String validateurNom;
    private String commentaireDecision;
    private boolean urgente;
    @Builder.Default
    private List<LigneBesoinDTO> lignes = new ArrayList<>();
}
