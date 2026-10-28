package com.ept.gestion.auth.controller;

import com.ept.gestion.auth.dto.ChangePasswordRequest;
import com.ept.gestion.auth.dto.LoginRequest;
import com.ept.gestion.auth.dto.LoginResponse;
import com.ept.gestion.auth.dto.UtilisateurDTO;
import com.ept.gestion.auth.service.AuthService;
import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification et la gestion de session.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints de connexion et profil utilisateur")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.succes("Connexion réussie", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté", description = "Retourne les informations de l'utilisateur à partir du JWT")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> me() {
        String email = SecurityUtils.getCurrentUserEmail();
        UtilisateurDTO profile = authService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.succes(profile));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer le mot de passe", description = "Modifie le mot de passe de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        authService.changerMotDePasse(email, request);
        return ResponseEntity.ok(ApiResponse.succes("Mot de passe mis à jour avec succès", null));
    }
}
