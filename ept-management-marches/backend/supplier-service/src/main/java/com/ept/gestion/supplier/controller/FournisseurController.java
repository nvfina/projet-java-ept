package com.ept.gestion.supplier.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.supplier.dto.FournisseurCreateRequest;
import com.ept.gestion.supplier.dto.FournisseurDTO;
import com.ept.gestion.supplier.dto.FournisseurSearchCriteria;
import com.ept.gestion.supplier.dto.FournisseurUpdateRequest;
import com.ept.gestion.supplier.service.FournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@Tag(name = "Fournisseurs", description = "Gestion du référentiel des fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Créer un fournisseur", description = "Réservé au Service Achats et Administrateur")
    public ResponseEntity<ApiResponse<FournisseurDTO>> creer(@Valid @RequestBody FournisseurCreateRequest request) {
        FournisseurDTO dto = fournisseurService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Fournisseur créé avec succès", dto));
    }

    @GetMapping
    @Operation(summary = "Lister les fournisseurs avec pagination")
    public ResponseEntity<ApiResponse<PageResponse<FournisseurDTO>>> lister(
            @PageableDefault(size = 10, sort = "raisonSociale") Pageable pageable) {
        PageResponse<FournisseurDTO> page = fournisseurService.listerTous(pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/recherche")
    @Operation(summary = "Recherche multicritères de fournisseurs")
    public ResponseEntity<ApiResponse<PageResponse<FournisseurDTO>>> rechercher(
            FournisseurSearchCriteria criteres,
            @PageableDefault(size = 10, sort = "raisonSociale") Pageable pageable) {
        PageResponse<FournisseurDTO> page = fournisseurService.rechercher(criteres, pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/actifs")
    @Operation(summary = "Lister tous les fournisseurs actifs")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> listerActifs() {
        return ResponseEntity.ok(ApiResponse.succes(fournisseurService.listerActifs()));
    }

    @GetMapping("/domaine/{domaine}")
    @Operation(summary = "Lister les fournisseurs par domaine")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> listerParDomaine(@PathVariable String domaine) {
        return ResponseEntity.ok(ApiResponse.succes(fournisseurService.listerParDomaine(domaine)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(fournisseurService.trouverParId(id)));
    }

    @GetMapping("/{id}/actif")
    @Operation(summary = "Vérifier si un fournisseur est actif (appel inter-service)")
    public ResponseEntity<ApiResponse<Boolean>> isActif(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(fournisseurService.isActif(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurDTO>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurUpdateRequest request) {
        FournisseurDTO dto = fournisseurService.modifier(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Fournisseur mis à jour", dto));
    }

    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Activer un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurDTO>> activer(@PathVariable Long id) {
        FournisseurDTO dto = fournisseurService.activer(id);
        return ResponseEntity.ok(ApiResponse.succes("Fournisseur activé", dto));
    }

    @PutMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Désactiver un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurDTO>> desactiver(@PathVariable Long id) {
        FournisseurDTO dto = fournisseurService.desactiver(id);
        return ResponseEntity.ok(ApiResponse.succes("Fournisseur désactivé", dto));
    }

    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Nombre total de fournisseurs")
    public ResponseEntity<ApiResponse<Long>> compter() {
        return ResponseEntity.ok(ApiResponse.succes(fournisseurService.compter()));
    }
}
