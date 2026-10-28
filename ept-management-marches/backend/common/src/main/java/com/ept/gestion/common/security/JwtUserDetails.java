package com.ept.gestion.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Détails de l'utilisateur authentifié extraits du jeton JWT.
 * Stockés dans les détails de l'authentification Spring Security.
 */
@Data
@AllArgsConstructor
public class JwtUserDetails {
    private Long userId;
    private String email;
    private List<String> roles;
    private String service;

    /**
     * Vérifie si l'utilisateur possède un rôle donné.
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
