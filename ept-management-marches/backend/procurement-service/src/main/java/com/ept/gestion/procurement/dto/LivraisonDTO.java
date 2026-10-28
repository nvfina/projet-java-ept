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
public class LivraisonDTO {
    private Long id;
    private Long lotId;
    private LocalDateTime dateLivraison;
    private Integer quantiteLivree;
    private String designation;
    private String observations;
    private Long receptionnaireId;
    private String receptionnaireNom;
    private Boolean conforme;
}
