package com.ept.gestion.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitaire pour récupérer les informations de l'utilisateur connecté
 * dans n'importe quel service.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Classe utilitaire non instanciable
    }

    /**
     * Retourne les détails de l'utilisateur connecté.
     */
    public static JwtUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtUserDetails) {
            return (JwtUserDetails) auth.getDetails();
        }
        return null;
    }

    /**
     * Retourne l'identifiant de l'utilisateur connecté.
     */
    public static Long getCurrentUserId() {
        JwtUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Retourne l'email de l'utilisateur connecté.
     */
    public static String getCurrentUserEmail() {
        JwtUserDetails user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Vérifie si l'utilisateur connecté possède un rôle donné.
     */
    public static boolean hasRole(String role) {
        JwtUserDetails user = getCurrentUser();
        return user != null && user.hasRole(role);
    }
}
