package com.ept.gestion.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueStatutDTO {
    private Long id;
    private String entiteType;
    private Long entiteId;
    private String ancienStatut;
    private String nouveauStatut;
    private LocalDateTime dateChangement;
    private Long utilisateurId;
    private String utilisateurNom;
    private String commentaire;
}
