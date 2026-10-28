package com.ept.gestion.procurement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivraisonCreateRequest {
    @NotNull(message = "L'identifiant du lot est obligatoire")
    private Long lotId;
    private LocalDateTime dateLivraison;
    @NotNull(message = "La quantité livrée est obligatoire")
    @Min(value = 1, message = "La quantité livrée doit être d'au moins 1")
    private Integer quantiteLivree;
    private String designation;
    private String observations;
    private Boolean conforme;
}
