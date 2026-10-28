package com.ept.gestion.procurement.service;

import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.common.enums.StatutOffre;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.security.JwtUserDetails;
import com.ept.gestion.common.security.SecurityUtils;
import com.ept.gestion.procurement.client.SupplierClient;
import com.ept.gestion.procurement.dto.*;
import com.ept.gestion.procurement.entity.*;
import com.ept.gestion.procurement.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LotService {

    private final LotRepository lotRepository;
    private final MarcheRepository marcheRepository;
    private final OffreTarifaireRepository offreRepository;
    private final LivraisonRepository livraisonRepository;
    private final HistoriqueStatutRepository historiqueRepository;
    private final SupplierClient supplierClient;
    private final MarcheService marcheService;

    public LotService(LotRepository lotRepository,
                      MarcheRepository marcheRepository,
                      OffreTarifaireRepository offreRepository,
                      LivraisonRepository livraisonRepository,
                      HistoriqueStatutRepository historiqueRepository,
                      SupplierClient supplierClient,
                      @Lazy MarcheService marcheService) {
        this.lotRepository = lotRepository;
        this.marcheRepository = marcheRepository;
        this.offreRepository = offreRepository;
        this.livraisonRepository = livraisonRepository;
        this.historiqueRepository = historiqueRepository;
        this.supplierClient = supplierClient;
        this.marcheService = marcheService;
    }

    @Transactional
    public LotDTO creer(Long marcheId, LotCreateRequest request) {
        Marche marche = marcheRepository.findById(marcheId)
                .orElseThrow(() -> new ResourceNotFoundException("Marché", "id", marcheId));

        if (marche.getStatut() == StatutMarche.CLOTURE || marche.getStatut() == StatutMarche.ANNULE) {
            throw new BusinessException("Impossible d'ajouter un lot à un marché clôturé ou annulé");
        }

        Lot lot = Lot.builder()
                .numero(request.getNumero())
                .designation(request.getDesignation().trim())
                .description(request.getDescription())
                .marche(marche)
                .statut(StatutLot.EN_ATTENTE)
                .montantEstime(request.getMontantEstime())
                .dateLivraisonPrevue(request.getDateLivraisonPrevue())
                .observations(request.getObservations())
                .tauxExecution(0.0)
                .lignesLot(new ArrayList<>())
                .build();

        if (request.getLignesLot() != null) {
            for (LigneLotRequest ligneReq : request.getLignesLot()) {
                BigDecimal pu = ligneReq.getPrixUnitaire() != null ? ligneReq.getPrixUnitaire() : BigDecimal.ZERO;
                BigDecimal mt = pu.multiply(BigDecimal.valueOf(ligneReq.getQuantite()));
                LigneLot ligne = LigneLot.builder()
                        .ligneBesoinId(ligneReq.getLigneBesoinId())
                        .designation(ligneReq.getDesignation().trim())
                        .quantite(ligneReq.getQuantite())
                        .prixUnitaire(pu)
                        .montantTotal(mt)
                        .build();
                lot.addLigneLot(ligne);
            }
        }

        Lot saved = lotRepository.save(lot);
        return toDtoStatic(saved);
    }

    @Transactional
    public LotDTO modifier(Long id, LotUpdateRequest request) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", id));

        if (lot.getStatut() == StatutLot.CLOTURE || lot.getStatut() == StatutLot.ANNULE) {
            throw new BusinessException("Impossible de modifier un lot clôturé ou annulé");
        }

        if (request.getDesignation() != null && !request.getDesignation().isBlank()) lot.setDesignation(request.getDesignation().trim());
        if (request.getDescription() != null) lot.setDescription(request.getDescription());
        if (request.getMontantEstime() != null) lot.setMontantEstime(request.getMontantEstime());
        if (request.getDateLivraisonPrevue() != null) lot.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        if (request.getObservations() != null) lot.setObservations(request.getObservations());

        return toDtoStatic(lotRepository.save(lot));
    }

    @Transactional
    public LotDTO attribuer(Long id, AttributionRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", id));

        // Règle : Vérifier que le fournisseur est actif
        if (!supplierClient.isFournisseurActif(request.getFournisseurId())) {
            throw new BusinessException("Ce fournisseur est inactif ou n'existe pas. Attribution impossible.");
        }

        String ancienStatut = lot.getStatut().name();
        lot.setFournisseurId(request.getFournisseurId());
        lot.setFournisseurNom(request.getFournisseurNom());
        lot.setMontantAttribue(request.getMontantAttribue());
        lot.setDateAttribution(LocalDateTime.now());
        if (request.getDateLivraisonPrevue() != null) {
            lot.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        }
        if (request.getObservations() != null) {
            lot.setObservations(request.getObservations());
        }
        lot.setStatut(StatutLot.ATTRIBUE);

        // Si une offreId est fournie, marquer l'offre comme RETENUE
        if (request.getOffreId() != null) {
            offreRepository.findById(request.getOffreId()).ifPresent(offre -> {
                offre.setStatut(StatutOffre.RETENUE);
                offre.setDateEvaluation(LocalDateTime.now());
                if (user != null) offre.setEvaluateurId(user.getUserId());
                offreRepository.save(offre);
            });
        }

        Lot saved = lotRepository.save(lot);

        // Mettre à jour le marché parent
        Marche marche = lot.getMarche();
        if (marche.getStatut() == StatutMarche.BROUILLON || marche.getStatut() == StatutMarche.EN_COURS) {
            marche.setStatut(StatutMarche.ATTRIBUE);
            marche.setDateAttribution(LocalDateTime.now());
            marcheRepository.save(marche);
        }
        marcheService.recalculerMontantTotal(marche.getId());

        enregistrerHistorique(saved.getId(), ancienStatut, StatutLot.ATTRIBUE.name(),
                user != null ? user.getUserId() : null,
                user != null ? user.getEmail() : "Système",
                "Attribution du lot au fournisseur : " + request.getFournisseurNom());

        return toDtoStatic(saved);
    }

    @Transactional(readOnly = true)
    public LotDTO trouverParId(Long id) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", id));
        return toDtoStatic(lot);
    }

    @Transactional(readOnly = true)
    public List<LotDTO> listerParMarche(Long marcheId) {
        return lotRepository.findByMarcheId(marcheId).stream().map(LotService::toDtoStatic).toList();
    }

    @Transactional
    public Double calculerTauxExecution(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", lotId));

        int quantiteTotaleDemandee = lot.getLignesLot().stream()
                .mapToInt(LigneLot::getQuantite)
                .sum();

        if (quantiteTotaleDemandee == 0) return 0.0;

        int quantiteLivree = livraisonRepository.findByLotId(lotId).stream()
                .filter(l -> l.getConforme() == null || l.getConforme())
                .mapToInt(Livraison::getQuantiteLivree)
                .sum();

        double taux = Math.min(100.0, ((double) quantiteLivree / quantiteTotaleDemandee) * 100.0);
        lot.setTauxExecution(Math.round(taux * 100.0) / 100.0);

        if (taux >= 100.0) {
            lot.setStatut(StatutLot.LIVRE);
            lot.setDateLivraisonEffective(LocalDateTime.now());
        } else if (taux > 0.0) {
            lot.setStatut(StatutLot.LIVRE_PARTIELLEMENT);
        }

        lotRepository.save(lot);
        return lot.getTauxExecution();
    }

    private void enregistrerHistorique(Long id, String ancien, String nouveau, Long userId, String userNom, String com) {
        HistoriqueStatut h = HistoriqueStatut.builder()
                .entiteType("LOT")
                .entiteId(id)
                .ancienStatut(ancien)
                .nouveauStatut(nouveau)
                .utilisateurId(userId)
                .utilisateurNom(userNom)
                .commentaire(com)
                .build();
        historiqueRepository.save(h);
    }

    public static LotDTO toDtoStatic(Lot l) {
        List<LigneLotDTO> lignes = l.getLignesLot() != null ? l.getLignesLot().stream()
                .map(ll -> LigneLotDTO.builder()
                        .id(ll.getId())
                        .ligneBesoinId(ll.getLigneBesoinId())
                        .designation(ll.getDesignation())
                        .quantite(ll.getQuantite())
                        .prixUnitaire(ll.getPrixUnitaire())
                        .montantTotal(ll.getMontantTotal())
                        .build())
                .toList() : List.of();

        return LotDTO.builder()
                .id(l.getId())
                .numero(l.getNumero())
                .designation(l.getDesignation())
                .description(l.getDescription())
                .marcheId(l.getMarche() != null ? l.getMarche().getId() : null)
                .marcheReference(l.getMarche() != null ? l.getMarche().getReference() : null)
                .statut(l.getStatut())
                .montantEstime(l.getMontantEstime())
                .montantAttribue(l.getMontantAttribue())
                .fournisseurId(l.getFournisseurId())
                .fournisseurNom(l.getFournisseurNom())
                .dateAttribution(l.getDateAttribution())
                .dateLivraisonPrevue(l.getDateLivraisonPrevue())
                .dateLivraisonEffective(l.getDateLivraisonEffective())
                .tauxExecution(l.getTauxExecution())
                .observations(l.getObservations())
                .lignesLot(lignes)
                .build();
    }
}
