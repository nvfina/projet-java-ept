package com.ept.gestion.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffreCreateRequest {
    @NotNull(message = "L'identifiant du lot est obligatoire")
    private Long lotId;
    @NotNull(message = "L'identifiant du fournisseur est obligatoire")
    private Long fournisseurId;
    private String fournisseurNom;
    private String devise;
    private String observations;

    @NotEmpty(message = "L'offre doit comporter au moins une ligne tarifaire")
    @Valid
    private List<LigneOffreRequest> lignesOffre;
}
