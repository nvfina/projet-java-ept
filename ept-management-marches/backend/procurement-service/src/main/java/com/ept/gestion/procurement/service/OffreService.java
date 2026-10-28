package com.ept.gestion.procurement.service;

import com.ept.gestion.common.enums.StatutOffre;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.security.JwtUserDetails;
import com.ept.gestion.common.security.SecurityUtils;
import com.ept.gestion.procurement.dto.*;
import com.ept.gestion.procurement.entity.LigneOffre;
import com.ept.gestion.procurement.entity.Lot;
import com.ept.gestion.procurement.entity.OffreTarifaire;
import com.ept.gestion.procurement.repository.LotRepository;
import com.ept.gestion.procurement.repository.OffreTarifaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OffreService {

    private final OffreTarifaireRepository offreRepository;
    private final LotRepository lotRepository;

    public OffreService(OffreTarifaireRepository offreRepository,
                        LotRepository lotRepository) {
        this.offreRepository = offreRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional
    public OffreDTO creer(OffreCreateRequest request) {
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", request.getLotId()));

        OffreTarifaire offre = OffreTarifaire.builder()
                .lotId(request.getLotId())
                .fournisseurId(request.getFournisseurId())
                .fournisseurNom(request.getFournisseurNom())
                .dateOffre(LocalDateTime.now())
                .statut(StatutOffre.SOUMISE)
                .devise(request.getDevise() != null ? request.getDevise() : "XOF")
                .observations(request.getObservations())
                .lignesOffre(new ArrayList<>())
                .build();

        BigDecimal montantTotalCalcule = BigDecimal.ZERO;

        if (request.getLignesOffre() != null) {
            for (LigneOffreRequest ligneReq : request.getLignesOffre()) {
                BigDecimal pu = ligneReq.getPrixUnitaire() != null ? ligneReq.getPrixUnitaire() : BigDecimal.ZERO;
                BigDecimal montantLigne = pu.multiply(BigDecimal.valueOf(ligneReq.getQuantite()));
                montantTotalCalcule = montantTotalCalcule.add(montantLigne);

                LigneOffre ligne = LigneOffre.builder()
                        .designation(ligneReq.getDesignation().trim())
                        .quantite(ligneReq.getQuantite())
                        .prixUnitaire(pu)
                        .montantLigne(montantLigne)
                        .observations(ligneReq.getObservations())
                        .build();
                offre.addLigneOffre(ligne);
            }
        }

        // Règle métier : Ne jamais faire confiance aux totaux envoyés par le client
        offre.setMontantTotal(montantTotalCalcule);

        OffreTarifaire saved = offreRepository.save(offre);
        return toDto(saved, lot.getDesignation());
    }

    @Transactional(readOnly = true)
    public List<OffreDTO> listerParLot(Long lotId) {
        Lot lot = lotRepository.findById(lotId).orElse(null);
        String lotNom = lot != null ? lot.getDesignation() : "";
        return offreRepository.findByLotId(lotId).stream()
                .map(o -> toDto(o, lotNom))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OffreDTO> comparerOffres(Long lotId) {
        Lot lot = lotRepository.findById(lotId).orElse(null);
        String lotNom = lot != null ? lot.getDesignation() : "";
        // Trié par montant total croissant pour faciliter le choix
        return offreRepository.findByLotIdOrderByMontantTotalAsc(lotId).stream()
                .map(o -> toDto(o, lotNom))
                .toList();
    }

    @Transactional
    public OffreDTO evaluer(Long id, EvaluationOffreRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        OffreTarifaire offre = offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre tarifaire", "id", id));

        offre.setStatut(request.getStatut());
        offre.setDateEvaluation(LocalDateTime.now());
        if (user != null) offre.setEvaluateurId(user.getUserId());
        if (request.getObservations() != null) {
            offre.setObservations(request.getObservations());
        }

        return toDto(offreRepository.save(offre), null);
    }

    @Transactional
    public OffreDTO retenir(Long id) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        OffreTarifaire offre = offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre tarifaire", "id", id));

        // Rejeter les autres offres du lot
        List<OffreTarifaire> offresDuLot = offreRepository.findByLotId(offre.getLotId());
        for (OffreTarifaire o : offresDuLot) {
            if (!o.getId().equals(id) && o.getStatut() == StatutOffre.SOUMISE) {
                o.setStatut(StatutOffre.REJETEE);
                offreRepository.save(o);
            }
        }

        offre.setStatut(StatutOffre.RETENUE);
        offre.setDateEvaluation(LocalDateTime.now());
        if (user != null) offre.setEvaluateurId(user.getUserId());

        return toDto(offreRepository.save(offre), null);
    }

    @Transactional(readOnly = true)
    public OffreDTO trouverParId(Long id) {
        OffreTarifaire offre = offreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre", "id", id));
        return toDto(offre, null);
    }

    private OffreDTO toDto(OffreTarifaire o, String lotDesignation) {
        List<LigneOffreDTO> lignes = o.getLignesOffre() != null ? o.getLignesOffre().stream()
                .map(lo -> LigneOffreDTO.builder()
                        .id(lo.getId())
                        .designation(lo.getDesignation())
                        .quantite(lo.getQuantite())
                        .prixUnitaire(lo.getPrixUnitaire())
                        .montantLigne(lo.getMontantLigne())
                        .observations(lo.getObservations())
                        .build())
                .toList() : List.of();

        return OffreDTO.builder()
                .id(o.getId())
                .lotId(o.getLotId())
                .lotDesignation(lotDesignation)
                .fournisseurId(o.getFournisseurId())
                .fournisseurNom(o.getFournisseurNom())
                .dateOffre(o.getDateOffre())
                .statut(o.getStatut())
                .montantTotal(o.getMontantTotal())
                .devise(o.getDevise())
                .observations(o.getObservations())
                .dateEvaluation(o.getDateEvaluation())
                .evaluateurId(o.getEvaluateurId())
                .lignesOffre(lignes)
                .build();
    }
}
