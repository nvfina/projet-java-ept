package com.ept.gestion.procurement.dto;

import com.ept.gestion.common.enums.StatutBesoin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneBesoinDTO {
    private Long id;
    private String designation;
    private String description;
    private Integer quantiteDemandee;
    private String unite;
    private BigDecimal estimationPrix;
    private String observations;
}
