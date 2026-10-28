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
public class DashboardDTO {
    private long totalDemandes;
    private long demandesEnAttente;
    private long demandesValidees;
    private long demandesRejetees;
    private long demandesEnCours;

    private long totalMarches;
    private long marchesEnCours;
    private long marchesClotures;

    private long totalLots;
    private long lotsAttribues;
    private long lotsEnCoursLivraison;
    private long lotsClotures;

    private Double tauxExecutionGlobal;
    private BigDecimal montantTotalMarches;
    private BigDecimal montantTotalDepenses;
    private long totalFournisseurs;
}
