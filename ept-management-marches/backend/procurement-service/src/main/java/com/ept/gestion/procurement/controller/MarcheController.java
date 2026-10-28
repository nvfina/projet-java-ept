package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.procurement.dto.LotDTO;
import com.ept.gestion.procurement.dto.MarcheCreateRequest;
import com.ept.gestion.procurement.dto.MarcheDTO;
import com.ept.gestion.procurement.dto.MarcheUpdateRequest;
import com.ept.gestion.procurement.service.LotService;
import com.ept.gestion.procurement.service.MarcheService;
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
@RequestMapping("/api/marches")
@Tag(name = "Marchés", description = "Gestion des marchés publics de matériels")
public class MarcheController {

    private final MarcheService marcheService;
    private final LotService lotService;

    public MarcheController(MarcheService marcheService, LotService lotService) {
        this.marcheService = marcheService;
        this.lotService = lotService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Créer un marché avec ses lots", description = "Réservé au Service Achats")
    public ResponseEntity<ApiResponse<MarcheDTO>> creer(@Valid @RequestBody MarcheCreateRequest request) {
        MarcheDTO dto = marcheService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Marché créé avec succès", dto));
    }

    @GetMapping
    @Operation(summary = "Lister les marchés avec pagination et filtres")
    public ResponseEntity<ApiResponse<PageResponse<MarcheDTO>>> lister(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) StatutMarche statut,
            @PageableDefault(size = 10, sort = "dateCreation") Pageable pageable) {
        PageResponse<MarcheDTO> page = marcheService.lister(recherche, statut, pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'un marché avec ses lots")
    public ResponseEntity<ApiResponse<MarcheDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(marcheService.trouverParId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un marché")
    public ResponseEntity<ApiResponse<MarcheDTO>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody MarcheUpdateRequest request) {
        MarcheDTO dto = marcheService.modifier(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Marché modifié", dto));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Changer le statut d'un marché")
    public ResponseEntity<ApiResponse<MarcheDTO>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutMarche statut) {
        MarcheDTO dto = marcheService.changerStatut(id, statut);
        return ResponseEntity.ok(ApiResponse.succes("Statut du marché mis à jour", dto));
    }

    @GetMapping("/{id}/lots")
    @Operation(summary = "Lister les lots d'un marché")
    public ResponseEntity<ApiResponse<List<LotDTO>>> listerLots(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(lotService.listerParMarche(id)));
    }
}
