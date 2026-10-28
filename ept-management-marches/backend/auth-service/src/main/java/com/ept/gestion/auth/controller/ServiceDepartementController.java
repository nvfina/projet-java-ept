package com.ept.gestion.auth.controller;

import com.ept.gestion.auth.dto.ServiceDTO;
import com.ept.gestion.auth.service.ServiceDepartementService;
import com.ept.gestion.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des départements et services.
 */
@RestController
@RequestMapping("/api/services")
@Tag(name = "Services / Départements", description = "Gestion des départements de l'EPT")
public class ServiceDepartementController {

    private final ServiceDepartementService serviceService;

    public ServiceDepartementController(ServiceDepartementService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les départements", description = "Accessible à tout utilisateur authentifié")
    public ResponseEntity<ApiResponse<List<ServiceDTO>>> listerTous() {
        return ResponseEntity.ok(ApiResponse.succes(serviceService.listerTous()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un département")
    public ResponseEntity<ApiResponse<ServiceDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(serviceService.trouverParId(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un département", description = "Réservé à l'administrateur")
    public ResponseEntity<ApiResponse<ServiceDTO>> creer(@Valid @RequestBody ServiceDTO dto) {
        ServiceDTO res = serviceService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes("Département créé", res));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un département")
    public ResponseEntity<ApiResponse<ServiceDTO>> modifier(@PathVariable Long id, @Valid @RequestBody ServiceDTO dto) {
        ServiceDTO res = serviceService.modifier(id, dto);
        return ResponseEntity.ok(ApiResponse.succes("Département modifié", res));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer ou désactiver un département")
    public ResponseEntity<ApiResponse<ServiceDTO>> changerStatut(@PathVariable Long id, @RequestParam boolean actif) {
        ServiceDTO res = serviceService.changerStatut(id, actif);
        return ResponseEntity.ok(ApiResponse.succes("Statut du département mis à jour", res));
    }
}
