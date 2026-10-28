package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.procurement.dto.LivraisonCreateRequest;
import com.ept.gestion.procurement.dto.LivraisonDTO;
import com.ept.gestion.procurement.service.LivraisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@Tag(name = "Livraisons", description = "Suivi des réceptions de matériels et équipements")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer une livraison pour un lot")
    public ResponseEntity<ApiResponse<LivraisonDTO>> enregistrer(@Valid @RequestBody LivraisonCreateRequest request) {
        LivraisonDTO dto = livraisonService.enregistrer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Livraison enregistrée avec succès", dto));
    }

    @GetMapping("/lot/{lotId}")
    @Operation(summary = "Lister les livraisons d'un lot")
    public ResponseEntity<ApiResponse<List<LivraisonDTO>>> listerParLot(@PathVariable Long lotId) {
        return ResponseEntity.ok(ApiResponse.succes(livraisonService.listerParLot(lotId)));
    }
}
