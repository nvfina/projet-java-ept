package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.procurement.service.StatistiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@Tag(name = "Statistiques", description = "Indicateurs et statistiques pour tableau de bord")
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    @GetMapping("/global")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Indicateurs statistiques globaux")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistiquesGlobales() {
        return ResponseEntity.ok(ApiResponse.succes(statistiqueService.getStatistiquesGlobales()));
    }

    @GetMapping("/depenses/service")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Répartition des dépenses par service demandeur")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepensesParService() {
        return ResponseEntity.ok(ApiResponse.succes(statistiqueService.getDepensesParService()));
    }

    @GetMapping("/depenses/marche")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Répartition des dépenses par marché")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepensesParMarche() {
        return ResponseEntity.ok(ApiResponse.succes(statistiqueService.getDepensesParMarche()));
    }
}
