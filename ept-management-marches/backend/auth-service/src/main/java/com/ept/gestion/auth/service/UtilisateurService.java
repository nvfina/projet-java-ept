package com.ept.gestion.auth.service;

import com.ept.gestion.auth.dto.RegisterRequest;
import com.ept.gestion.auth.dto.UpdateUtilisateurRequest;
import com.ept.gestion.auth.dto.UtilisateurDTO;
import com.ept.gestion.auth.entity.Utilisateur;
import com.ept.gestion.auth.repository.UtilisateurRepository;
import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.RoleEnum;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des comptes utilisateurs (CRUD, activation/désactivation).
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilisateurDTO creer(RegisterRequest req) {
        String cleanEmail = req.getEmail().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(cleanEmail)) {
            throw new BusinessException("Un utilisateur existe déjà avec cet e-mail : " + cleanEmail);
        }

        Utilisateur u = Utilisateur.builder()
                .nom(req.getNom().trim().toUpperCase())
                .prenom(req.getPrenom().trim())
                .email(cleanEmail)
                .motDePasse(passwordEncoder.encode(req.getMotDePasse()))
                .telephone(req.getTelephone())
                .role(req.getRole())
                .serviceNom(req.getServiceNom())
                .actif(true)
                .build();

        return AuthService.toDto(utilisateurRepository.save(u));
    }

    @Transactional
    public UtilisateurDTO modifier(Long id, UpdateUtilisateurRequest req) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        if (req.getNom() != null && !req.getNom().isBlank()) {
            u.setNom(req.getNom().trim().toUpperCase());
        }
        if (req.getPrenom() != null && !req.getPrenom().isBlank()) {
            u.setPrenom(req.getPrenom().trim());
        }
        if (req.getTelephone() != null) {
            u.setTelephone(req.getTelephone().trim());
        }
        if (req.getServiceNom() != null) {
            u.setServiceNom(req.getServiceNom().trim());
        }

        return AuthService.toDto(utilisateurRepository.save(u));
    }

    @Transactional(readOnly = true)
    public UtilisateurDTO trouverParId(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        return AuthService.toDto(u);
    }

    @Transactional(readOnly = true)
    public PageResponse<UtilisateurDTO> listerTous(Pageable pageable) {
        Page<Utilisateur> page = utilisateurRepository.findAll(pageable);
        List<UtilisateurDTO> dtos = page.getContent().stream()
                .map(AuthService::toDto)
                .toList();

        return PageResponse.<UtilisateurDTO>builder()
                .contenu(dtos)
                .page(page.getNumber())
                .taille(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .premier(page.isFirst())
                .dernier(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDTO> listerParRole(RoleEnum role) {
        return utilisateurRepository.findByRole(role).stream()
                .map(AuthService::toDto)
                .toList();
    }

    @Transactional
    public UtilisateurDTO activer(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        u.setActif(true);
        return AuthService.toDto(utilisateurRepository.save(u));
    }

    @Transactional
    public UtilisateurDTO desactiver(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        u.setActif(false);
        return AuthService.toDto(utilisateurRepository.save(u));
    }
}
