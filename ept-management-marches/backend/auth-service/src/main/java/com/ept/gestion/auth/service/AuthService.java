package com.ept.gestion.auth.service;

import com.ept.gestion.auth.dto.ChangePasswordRequest;
import com.ept.gestion.auth.dto.LoginRequest;
import com.ept.gestion.auth.dto.LoginResponse;
import com.ept.gestion.auth.dto.UtilisateurDTO;
import com.ept.gestion.auth.entity.Utilisateur;
import com.ept.gestion.auth.repository.UtilisateurRepository;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.exception.UnauthorizedException;
import com.ept.gestion.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour l'authentification et la gestion du profil.
 */
@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authentifie un utilisateur avec son email et mot de passe et retourne un jeton JWT.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Identifiants de connexion invalides"));

        if (!user.isActif()) {
            throw new BusinessException("Ce compte a été désactivé. Veuillez contacter l'administrateur.");
        }

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new UnauthorizedException("Identifiants de connexion invalides");
        }

        user.setDateDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                List.of(user.getRole().name()),
                user.getServiceNom()
        );

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole().name())
                .service(user.getServiceNom())
                .expiresIn(expirationMs)
                .build();
    }

    /**
     * Récupère le profil de l'utilisateur par son email.
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getProfile(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email));
        return toDto(user);
    }

    /**
     * Permet à un utilisateur connecté de modifier son mot de passe.
     */
    @Transactional
    public void changerMotDePasse(String email, ChangePasswordRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getAncienMotDePasse(), user.getMotDePasse())) {
            throw new BusinessException("L'ancien mot de passe est incorrect");
        }

        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
    }

    public static UtilisateurDTO toDto(Utilisateur u) {
        return UtilisateurDTO.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .telephone(u.getTelephone())
                .actif(u.isActif())
                .role(u.getRole())
                .serviceNom(u.getServiceNom())
                .dateCreation(u.getDateCreation())
                .dateDerniereConnexion(u.getDateDerniereConnexion())
                .build();
    }
}
