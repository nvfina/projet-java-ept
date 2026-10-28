package com.ept.gestion.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BesoinCreateRequest {
    @NotBlank(message = "La description est obligatoire")
    private String description;
    private String motif;
    private boolean urgente;
    @NotEmpty(message = "L'expression de besoin doit comporter au moins un article")
    @Valid
    private List<LigneBesoinRequest> lignes;
}
