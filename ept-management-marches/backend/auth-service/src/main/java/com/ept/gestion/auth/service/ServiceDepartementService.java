package com.ept.gestion.auth.service;

import com.ept.gestion.auth.dto.ServiceDTO;
import com.ept.gestion.auth.entity.ServiceDepartement;
import com.ept.gestion.auth.repository.ServiceDepartementRepository;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier pour la gestion des départements et services de l'EPT.
 */
@Service
public class ServiceDepartementService {

    private final ServiceDepartementRepository repository;

    public ServiceDepartementService(ServiceDepartementRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ServiceDTO creer(ServiceDTO dto) {
        if (repository.existsByNom(dto.getNom())) {
            throw new BusinessException("Un département existe déjà avec ce nom : " + dto.getNom());
        }

        ServiceDepartement s = ServiceDepartement.builder()
                .nom(dto.getNom().trim())
                .description(dto.getDescription())
                .actif(dto.isActif())
                .build();

        return toDto(repository.save(s));
    }

    @Transactional
    public ServiceDTO modifier(Long id, ServiceDTO dto) {
        ServiceDepartement s = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            s.setNom(dto.getNom().trim());
        }
        if (dto.getDescription() != null) {
            s.setDescription(dto.getDescription());
        }

        return toDto(repository.save(s));
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> listerTous() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ServiceDTO trouverParId(Long id) {
        ServiceDepartement s = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));
        return toDto(s);
    }

    @Transactional
    public ServiceDTO changerStatut(Long id, boolean actif) {
        ServiceDepartement s = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));
        s.setActif(actif);
        return toDto(repository.save(s));
    }

    private ServiceDTO toDto(ServiceDepartement s) {
        return ServiceDTO.builder()
                .id(s.getId())
                .nom(s.getNom())
                .description(s.getDescription())
                .actif(s.isActif())
                .build();
    }
}
