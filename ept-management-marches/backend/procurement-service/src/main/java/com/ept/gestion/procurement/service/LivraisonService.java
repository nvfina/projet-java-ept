package com.ept.gestion.procurement.service;

import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.security.JwtUserDetails;
import com.ept.gestion.common.security.SecurityUtils;
import com.ept.gestion.procurement.dto.LivraisonCreateRequest;
import com.ept.gestion.procurement.dto.LivraisonDTO;
import com.ept.gestion.procurement.entity.LigneLot;
import com.ept.gestion.procurement.entity.Livraison;
import com.ept.gestion.procurement.entity.Lot;
import com.ept.gestion.procurement.repository.LivraisonRepository;
import com.ept.gestion.procurement.repository.LotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final LotRepository lotRepository;
    private final LotService lotService;

    public LivraisonService(LivraisonRepository livraisonRepository,
                            LotRepository lotRepository,
                            LotService lotService) {
        this.livraisonRepository = livraisonRepository;
        this.lotRepository = lotRepository;
        this.lotService = lotService;
    }

    @Transactional
    public LivraisonDTO enregistrer(LivraisonCreateRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Lot", "id", request.getLotId()));

        if (lot.getStatut() == StatutLot.EN_ATTENTE) {
            throw new BusinessException("Impossible d'enregistrer une livraison pour un lot non encore attribué");
        }

        int quantiteTotaleDemandee = lot.getLignesLot().stream()
                .mapToInt(LigneLot::getQuantite)
                .sum();

        int dejaLivre = livraisonRepository.findByLotId(lot.getId()).stream()
                .filter(l -> l.getConforme() == null || l.getConforme())
                .mapToInt(Livraison::getQuantiteLivree)
                .sum();

        if (dejaLivre + request.getQuantiteLivree() > quantiteTotaleDemandee) {
            throw new BusinessException(String.format(
                    "Quantité livrée (%d) + déjà livrée (%d) dépasse la quantité attribuée (%d)",
                    request.getQuantiteLivree(), dejaLivre, quantiteTotaleDemandee));
        }

        Livraison livraison = Livraison.builder()
                .lotId(request.getLotId())
                .dateLivraison(request.getDateLivraison() != null ? request.getDateLivraison() : LocalDateTime.now())
                .quantiteLivree(request.getQuantiteLivree())
                .designation(request.getDesignation())
                .observations(request.getObservations())
                .conforme(request.getConforme() != null ? request.getConforme() : true)
                .receptionnaireId(user != null ? user.getUserId() : null)
                .receptionnaireNom(user != null ? user.getEmail() : null)
                .build();

        Livraison saved = livraisonRepository.save(livraison);

        // Recalculer le taux d'exécution du lot
        lotService.calculerTauxExecution(lot.getId());

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<LivraisonDTO> listerParLot(Long lotId) {
        return livraisonRepository.findByLotId(lotId).stream().map(this::toDto).toList();
    }

    private LivraisonDTO toDto(Livraison l) {
        return LivraisonDTO.builder()
                .id(l.getId())
                .lotId(l.getLotId())
                .dateLivraison(l.getDateLivraison())
                .quantiteLivree(l.getQuantiteLivree())
                .designation(l.getDesignation())
                .observations(l.getObservations())
                .receptionnaireId(l.getReceptionnaireId())
                .receptionnaireNom(l.getReceptionnaireNom())
                .conforme(l.getConforme())
                .build();
    }
}
