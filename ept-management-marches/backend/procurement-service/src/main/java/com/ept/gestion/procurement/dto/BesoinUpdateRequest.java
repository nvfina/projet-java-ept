package com.ept.gestion.procurement.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BesoinUpdateRequest {
    private String description;
    private String motif;
    private Boolean urgente;
    @Valid
    private List<LigneBesoinRequest> lignes;
}
