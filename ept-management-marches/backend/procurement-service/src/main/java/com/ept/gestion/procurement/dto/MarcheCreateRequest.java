package com.ept.gestion.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class MarcheCreateRequest {
    @NotBlank(message = "L'objet du marché est obligatoire")
    private String objet;
    private String description;
    private BigDecimal budgetPrevisionnel;
    private LocalDateTime dateLancement;
    private String observations;

    @Valid
    private List<LotCreateRequest> lots;
}
