package com.ept.gestion.supplier.service;

import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.supplier.dto.FournisseurCreateRequest;
import com.ept.gestion.supplier.dto.FournisseurDTO;
import com.ept.gestion.supplier.dto.FournisseurSearchCriteria;
import com.ept.gestion.supplier.dto.FournisseurUpdateRequest;
import com.ept.gestion.supplier.entity.Fournisseur;
import com.ept.gestion.supplier.repository.FournisseurRepository;
import com.ept.gestion.supplier.specification.FournisseurSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurService(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    @Transactional
    public FournisseurDTO creer(FournisseurCreateRequest request) {
        if (request.getRaisonSociale() != null &&
                fournisseurRepository.findByRaisonSocialeContainingIgnoreCase(request.getRaisonSociale().trim(), Pageable.unpaged()).getTotalElements() > 0) {
            // Check exact match
            boolean exists = fournisseurRepository.findAll().stream()
                    .anyMatch(f -> f.getRaisonSociale().equalsIgnoreCase(request.getRaisonSociale().trim()));
            if (exists) {
                throw new BusinessException("Un fournisseur avec cette raison sociale existe déjà : " + request.getRaisonSociale());
            }
        }

        if (request.getNinea() != null && !request.getNinea().isBlank()) {
            if (fournisseurRepository.findByNinea(request.getNinea().trim()).isPresent()) {
                throw new BusinessException("Un fournisseur avec ce NINEA existe déjà : " + request.getNinea());
            }
        }

        Fournisseur fournisseur = Fournisseur.builder()
                .raisonSociale(request.getRaisonSociale().trim())
                .ninea(request.getNinea() != null ? request.getNinea().trim() : null)
                .adresse(request.getAdresse())
                .telephone(request.getTelephone().trim())
                .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null)
                .domaineIntervention(request.getDomaineIntervention().trim())
                .siteWeb(request.getSiteWeb())
                .contactPrincipal(request.getContactPrincipal())
                .observations(request.getObservations())
                .actif(true)
                .build();

        return toDto(fournisseurRepository.save(fournisseur));
    }

    @Transactional
    public FournisseurDTO modifier(Long id, FournisseurUpdateRequest request) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));

        if (request.getRaisonSociale() != null && !request.getRaisonSociale().isBlank()) {
            String newNom = request.getRaisonSociale().trim();
            if (!f.getRaisonSociale().equalsIgnoreCase(newNom)) {
                boolean exists = fournisseurRepository.findAll().stream()
                        .anyMatch(other -> !other.getId().equals(id) && other.getRaisonSociale().equalsIgnoreCase(newNom));
                if (exists) {
                    throw new BusinessException("Un fournisseur avec cette raison sociale existe déjà : " + newNom);
                }
                f.setRaisonSociale(newNom);
            }
        }

        if (request.getAdresse() != null) f.setAdresse(request.getAdresse());
        if (request.getTelephone() != null && !request.getTelephone().isBlank()) f.setTelephone(request.getTelephone().trim());
        if (request.getEmail() != null) f.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getDomaineIntervention() != null && !request.getDomaineIntervention().isBlank()) f.setDomaineIntervention(request.getDomaineIntervention().trim());
        if (request.getSiteWeb() != null) f.setSiteWeb(request.getSiteWeb());
        if (request.getContactPrincipal() != null) f.setContactPrincipal(request.getContactPrincipal());
        if (request.getObservations() != null) f.setObservations(request.getObservations());

        return toDto(fournisseurRepository.save(f));
    }

    @Transactional(readOnly = true)
    public FournisseurDTO trouverParId(Long id) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));
        return toDto(f);
    }

    @Transactional(readOnly = true)
    public PageResponse<FournisseurDTO> listerTous(Pageable pageable) {
        Page<Fournisseur> page = fournisseurRepository.findAll(pageable);
        List<FournisseurDTO> dtos = page.getContent().stream().map(this::toDto).toList();
        return PageResponse.<FournisseurDTO>builder()
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
    public PageResponse<FournisseurDTO> rechercher(FournisseurSearchCriteria criteres, Pageable pageable) {
        Page<Fournisseur> page = fournisseurRepository.findAll(FournisseurSpecification.avecCriteres(criteres), pageable);
        List<FournisseurDTO> dtos = page.getContent().stream().map(this::toDto).toList();
        return PageResponse.<FournisseurDTO>builder()
                .contenu(dtos)
                .page(page.getNumber())
                .taille(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .premier(page.isFirst())
                .dernier(page.isLast())
                .build();
    }

    @Transactional
    public FournisseurDTO activer(Long id) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));
        f.setActif(true);
        return toDto(fournisseurRepository.save(f));
    }

    @Transactional
    public FournisseurDTO desactiver(Long id) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));
        f.setActif(false);
        return toDto(fournisseurRepository.save(f));
    }

    @Transactional(readOnly = true)
    public List<FournisseurDTO> listerActifs() {
        return fournisseurRepository.findByActif(true, Pageable.unpaged()).getContent()
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<FournisseurDTO> listerParDomaine(String domaine) {
        return fournisseurRepository.findByDomaineInterventionContainingIgnoreCase(domaine)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public long compter() {
        return fournisseurRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean isActif(Long id) {
        return fournisseurRepository.findById(id)
                .map(Fournisseur::isActif)
                .orElse(false);
    }

    public FournisseurDTO toDto(Fournisseur f) {
        return FournisseurDTO.builder()
                .id(f.getId())
                .raisonSociale(f.getRaisonSociale())
                .ninea(f.getNinea())
                .adresse(f.getAdresse())
                .telephone(f.getTelephone())
                .email(f.getEmail())
                .domaineIntervention(f.getDomaineIntervention())
                .siteWeb(f.getSiteWeb())
                .contactPrincipal(f.getContactPrincipal())
                .actif(f.isActif())
                .dateCreation(f.getDateCreation())
                .dateModification(f.getDateModification())
                .observations(f.getObservations())
                .build();
    }
}
