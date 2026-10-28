package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.procurement.dto.EvaluationOffreRequest;
import com.ept.gestion.procurement.dto.OffreCreateRequest;
import com.ept.gestion.procurement.dto.OffreDTO;
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
@RequestMapping("/api/offres")
@Tag(name = "Offres Tarifaires", description = "Enregistrement, évaluation et sélection des offres fournisseurs")
public class OffreController {

    private final OffreService offreService;

    public OffreController(OffreService offreService) {
        this.offreService = offreService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer une offre fournisseur pour un lot")
    public ResponseEntity<ApiResponse<OffreDTO>> creer(@Valid @RequestBody OffreCreateRequest request) {
        OffreDTO dto = offreService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.succes("Offre enregistrée", dto));
    }

    @GetMapping("/lot/{lotId}")
    @Operation(summary = "Lister les offres pour un lot")
    public ResponseEntity<ApiResponse<List<OffreDTO>>> listerParLot(@PathVariable Long lotId) {
        return ResponseEntity.ok(ApiResponse.succes(offreService.listerParLot(lotId)));
    }

    @GetMapping("/lot/{lotId}/comparer")
    @Operation(summary = "Comparer les offres d'un lot triées par montant croissant")
    public ResponseEntity<ApiResponse<List<OffreDTO>>> comparer(@PathVariable Long lotId) {
        return ResponseEntity.ok(ApiResponse.succes(offreService.comparerOffres(lotId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'une offre")
    public ResponseEntity<ApiResponse<OffreDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(offreService.trouverParId(id)));
    }

    @PutMapping("/{id}/evaluer")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Évaluer une offre")
    public ResponseEntity<ApiResponse<OffreDTO>> evaluer(@PathVariable Long id, @Valid @RequestBody EvaluationOffreRequest request) {
        OffreDTO dto = offreService.evaluer(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Offre évaluée", dto));
    }

    @PutMapping("/{id}/retenir")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Retenir une offre pour attribution")
    public ResponseEntity<ApiResponse<OffreDTO>> retenir(@PathVariable Long id) {
        OffreDTO dto = offreService.retenir(id);
        return ResponseEntity.ok(ApiResponse.succes("Offre retenue avec succès", dto));
    }
}
