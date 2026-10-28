package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutOffre;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationOffreRequest {
    @NotNull(message = "Le statut est obligatoire")
    private StatutOffre statut;
    private String observations;
}
