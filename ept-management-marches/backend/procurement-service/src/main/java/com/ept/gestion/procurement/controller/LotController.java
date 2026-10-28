package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.procurement.dto.*;
import com.ept.gestion.procurement.service.LivraisonService;
import com.ept.gestion.procurement.service.LotService;
import com.ept.gestion.procurement.service.OffreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
@Tag(name = "Lots", description = "Gestion des lots, attributions et suivi d'exécution")
public class LotController {

    private final LotService lotService;
    private final OffreService offreService;
    private final LivraisonService livraisonService;

    public LotController(LotService lotService,
                         OffreService offreService,
                         LivraisonService livraisonService) {
        this.lotService = lotService;
        this.offreService = offreService;
        this.livraisonService = livraisonService;
    }

    @PostMapping("/marche/{marcheId}")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter un lot à un marché existant")
    public ResponseEntity<ApiResponse<LotDTO>> creer(@PathVariable Long marcheId, @Valid @RequestBody LotCreateRequest request) {
        LotDTO dto = lotService.creer(marcheId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes("Lot créé avec succès", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'un lot")
    public ResponseEntity<ApiResponse<LotDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(lotService.trouverParId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Modifier les informations d'un lot")
    public ResponseEntity<ApiResponse<LotDTO>> modifier(@PathVariable Long id, @Valid @RequestBody LotUpdateRequest request) {
        LotDTO dto = lotService.modifier(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Lot mis à jour", dto));
    }

    @PutMapping("/{id}/attribuer")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Attribuer un lot à un fournisseur sélectionné")
    public ResponseEntity<ApiResponse<LotDTO>> attribuer(@PathVariable Long id, @Valid @RequestBody AttributionRequest request) {
        LotDTO dto = lotService.attribuer(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Lot attribué avec succès", dto));
    }

    @GetMapping("/{id}/offres")
    @Operation(summary = "Consulter toutes les offres tarifaires reçues pour ce lot")
    public ResponseEntity<ApiResponse<List<OffreDTO>>> getOffres(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(offreService.listerParLot(id)));
    }

    @GetMapping("/{id}/offres/comparer")
    @Operation(summary = "Comparer les offres d'un lot triées par prix")
    public ResponseEntity<ApiResponse<List<OffreDTO>>> comparerOffres(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(offreService.comparerOffres(id)));
    }

    @GetMapping("/{id}/livraisons")
    @Operation(summary = "Consulter l'historique des livraisons pour ce lot")
    public ResponseEntity<ApiResponse<List<LivraisonDTO>>> getLivraisons(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(livraisonService.listerParLot(id)));
    }

    @GetMapping("/{id}/taux-execution")
    @Operation(summary = "Calculer et obtenir le taux d'exécution actuel du lot")
    public ResponseEntity<ApiResponse<Double>> getTauxExecution(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(lotService.calculerTauxExecution(id)));
    }
}
