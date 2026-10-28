package com.ept.gestion.dashboard.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.dashboard.dto.DashboardDTO;
import com.ept.gestion.dashboard.dto.DepenseMarcheDTO;
import com.ept.gestion.dashboard.dto.DepenseServiceDTO;
import com.ept.gestion.dashboard.dto.EvolutionDTO;
import com.ept.gestion.dashboard.dto.TopFournisseurDTO;
import com.ept.gestion.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Tableau de Bord", description = "Indicateurs décisionnels et suivi global pour la Direction")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/indicateurs")
    @PreAuthorize("hasAnyRole('DIRECTION', 'SERVICE_ACHATS', 'ADMIN', 'CHEF_SERVICE')")
    @Operation(summary = "Indicateurs clés du tableau de bord")
    public ResponseEntity<ApiResponse<DashboardDTO>> getIndicateurs() {
        return ResponseEntity.ok(ApiResponse.succes(dashboardService.getIndicateurs()));
    }

    @GetMapping("/depenses/service")
    @PreAuthorize("hasAnyRole('DIRECTION', 'SERVICE_ACHATS', 'ADMIN', 'CHEF_SERVICE')")
    @Operation(summary = "Dépenses réparties par service demandeur")
    public ResponseEntity<ApiResponse<List<DepenseServiceDTO>>> getDepensesParService() {
        return ResponseEntity.ok(ApiResponse.succes(dashboardService.getDepensesParService()));
    }

    @GetMapping("/depenses/marche")
    @PreAuthorize("hasAnyRole('DIRECTION', 'SERVICE_ACHATS', 'ADMIN', 'CHEF_SERVICE')")
    @Operation(summary = "Dépenses et budgets par marché")
    public ResponseEntity<ApiResponse<List<DepenseMarcheDTO>>> getDepensesParMarche() {
        return ResponseEntity.ok(ApiResponse.succes(dashboardService.getDepensesParMarche()));
    }

    @GetMapping("/evolution")
    @PreAuthorize("hasAnyRole('DIRECTION', 'SERVICE_ACHATS', 'ADMIN', 'CHEF_SERVICE')")
    @Operation(summary = "Évolution temporelle des demandes et dépenses")
    public ResponseEntity<ApiResponse<List<EvolutionDTO>>> getEvolution() {
        return ResponseEntity.ok(ApiResponse.succes(dashboardService.getEvolution()));
    }

    @GetMapping("/top-fournisseurs")
    @PreAuthorize("hasAnyRole('DIRECTION', 'SERVICE_ACHATS', 'ADMIN', 'CHEF_SERVICE')")
    @Operation(summary = "Classement des principaux fournisseurs")
    public ResponseEntity<ApiResponse<List<TopFournisseurDTO>>> getTopFournisseurs() {
        return ResponseEntity.ok(ApiResponse.succes(dashboardService.getTopFournisseurs()));
    }
}
