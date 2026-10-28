package com.ept.gestion.auth.controller;

import com.ept.gestion.auth.dto.RegisterRequest;
import com.ept.gestion.auth.dto.UpdateUtilisateurRequest;
import com.ept.gestion.auth.dto.UtilisateurDTO;
import com.ept.gestion.auth.service.UtilisateurService;
import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.RoleEnum;
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

/**
 * Contrôleur REST pour l'administration des utilisateurs.
 */
@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs EPT")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un utilisateur", description = "Réservé aux administrateurs")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> creer(@Valid @RequestBody RegisterRequest request) {
        UtilisateurDTO dto = utilisateurService.creer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.succes("Utilisateur créé avec succès", dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs avec pagination", description = "Réservé aux administrateurs")
    public ResponseEntity<ApiResponse<PageResponse<UtilisateurDTO>>> lister(
            @PageableDefault(size = 10, sort = "nom") Pageable pageable) {
        PageResponse<UtilisateurDTO> page = utilisateurService.listerTous(pageable);
        return ResponseEntity.ok(ApiResponse.succes(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal")
    @Operation(summary = "Consulter un utilisateur par identifiant")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> trouverParId(@PathVariable Long id) {
        UtilisateurDTO dto = utilisateurService.trouverParId(id);
        return ResponseEntity.ok(ApiResponse.succes(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un utilisateur", description = "Réservé aux administrateurs")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        UtilisateurDTO dto = utilisateurService.modifier(id, request);
        return ResponseEntity.ok(ApiResponse.succes("Utilisateur mis à jour", dto));
    }

    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un compte utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> activer(@PathVariable Long id) {
        UtilisateurDTO dto = utilisateurService.activer(id);
        return ResponseEntity.ok(ApiResponse.succes("Compte utilisateur activé", dto));
    }

    @PutMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un compte utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> desactiver(@PathVariable Long id) {
        UtilisateurDTO dto = utilisateurService.desactiver(id);
        return ResponseEntity.ok(ApiResponse.succes("Compte utilisateur désactivé", dto));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICE_ACHATS') or hasRole('DIRECTION')")
    @Operation(summary = "Lister les utilisateurs par rôle")
    public ResponseEntity<ApiResponse<List<UtilisateurDTO>>> listerParRole(@PathVariable RoleEnum role) {
        List<UtilisateurDTO> dtos = utilisateurService.listerParRole(role);
        return ResponseEntity.ok(ApiResponse.succes(dtos));
    }
}
