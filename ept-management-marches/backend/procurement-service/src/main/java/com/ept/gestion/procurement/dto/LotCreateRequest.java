package com.ept.gestion.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotCreateRequest {
    @NotNull(message = "Le numéro de lot est obligatoire")
    @Min(value = 1, message = "Le numéro de lot doit être au moins 1")
    private Integer numero;

    @NotBlank(message = "La désignation du lot est obligatoire")
    private String designation;

    private String description;
    private BigDecimal montantEstime;
    private LocalDateTime dateLivraisonPrevue;
    private String observations;

    @Valid
    private List<LigneLotRequest> lignesLot;
}
