package com.ept.gestion.procurement.controller;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.procurement.dto.BesoinCreateRequest;
import com.ept.gestion.procurement.dto.BesoinDTO;
import com.ept.gestion.procurement.dto.BesoinUpdateRequest;
import com.ept.gestion.procurement.dto.HistoriqueStatutDTO;
import com.ept.gestion.procurement.dto.ValidationBesoinRequest;
import com.ept.gestion.procurement.service.BesoinService;
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
@RequestMapping("/api/besoins")
@Tag(name = "Expressions de Besoins", description = "Gestion du cycle de vie des expressions de besoins")
public class BesoinController {

    private final BesoinService besoinService;

    public BesoinController(BesoinService besoinService) {
        this.besoinService = besoinService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CHEF_SERVICE') or hasRole('ADMIN')")
    @Operation(summary = "Créer une expression de besoin", description = "Réservé aux Chefs de Service et Admin")
    public ResponseEntity<ApiResponse<BesoinDTO>> creer(@Valid @RequestBody BesoinCreateRequest request) {
        BesoinDTO dto = besoinService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Expression de besoin créée avec succès", dto));
    }

    @GetMapping
    @Operation(summary = "Lister les expressions de besoins", description = "Filtre par rôle (Chef voit les siennes, Achats/Direction voient tout)")
    public ResponseEntity<ApiResponse<PageResponse<BesoinDTO>>> lister(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) StatutBesoin statut,
            @PageableDefault(size = 10, sort = "dateCreation") Pageable pageable) {
        PageResponse<BesoinDTO> page = besoinService.lister(service, statut, pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/mes-besoins")
    @PreAuthorize("hasRole('CHEF_SERVICE') or hasRole('ADMIN')")
    @Operation(summary = "Lister mes propres expressions de besoins")
    public ResponseEntity<ApiResponse<PageResponse<BesoinDTO>>> mesBesoins(
            @PageableDefault(size = 10, sort = "dateCreation") Pageable pageable) {
        PageResponse<BesoinDTO> page = besoinService.listerMesBesoins(pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/valides")
    @Operation(summary = "Lister les besoins validés disponibles pour constitution de marchés")
    public ResponseEntity<ApiResponse<List<BesoinDTO>>> listerValides() {
        return ResponseEntity.ok(ApiResponse.succes(besoinService.listerBesoinsValides()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'une expression de besoin")
    public ResponseEntity<ApiResponse<BesoinDTO>> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(besoinService.trouverParId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_SERVICE') or hasRole('ADMIN')")
    @Operation(summary = "Modifier une expression de besoin (si statut EN_ATTENTE)")
    public ResponseEntity<ApiResponse<BesoinDTO>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody BesoinUpdateRequest request) {
        BesoinDTO dto = besoinService.modifier(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Expression de besoin modifiée", dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_SERVICE') or hasRole('ADMIN')")
    @Operation(summary = "Annuler une expression de besoin (si statut EN_ATTENTE)")
    public ResponseEntity<ApiResponse<Void>> annuler(@PathVariable Long id) {
        besoinService.annuler(id);
        return ResponseEntity.ok(ApiResponse.succes("Expression de besoin annulée avec succès", null));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Valider une expression de besoin", description = "Réservé au Service Achats")
    public ResponseEntity<ApiResponse<BesoinDTO>> valider(
            @PathVariable Long id,
            @RequestBody(required = false) ValidationBesoinRequest request) {
        BesoinDTO dto = besoinService.valider(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Expression de besoin validée avec succès", dto));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Rejeter une expression de besoin", description = "Réservé au Service Achats")
    public ResponseEntity<ApiResponse<BesoinDTO>> rejeter(
            @PathVariable Long id,
            @RequestBody(required = false) ValidationBesoinRequest request) {
        BesoinDTO dto = besoinService.rejeter(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Expression de besoin rejetée", dto));
    }

    @GetMapping("/{id}/historique")
    @Operation(summary = "Consulter l'historique des changements de statut d'un besoin")
    public ResponseEntity<ApiResponse<List<HistoriqueStatutDTO>>> historique(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.succes(besoinService.getHistorique(id)));
    }
}
