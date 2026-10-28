package com.ept.gestion.procurement.service;

import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.security.JwtUserDetails;
import com.ept.gestion.common.security.SecurityUtils;
import com.ept.gestion.procurement.dto.*;
import com.ept.gestion.procurement.entity.*;
import com.ept.gestion.procurement.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarcheService {

    private final MarcheRepository marcheRepository;
    private final LotRepository lotRepository;
    private final ExpressionBesoinRepository besoinRepository;
    private final HistoriqueStatutRepository historiqueRepository;

    public MarcheService(MarcheRepository marcheRepository,
                         LotRepository lotRepository,
                         ExpressionBesoinRepository besoinRepository,
                         HistoriqueStatutRepository historiqueRepository) {
        this.marcheRepository = marcheRepository;
        this.lotRepository = lotRepository;
        this.besoinRepository = besoinRepository;
        this.historiqueRepository = historiqueRepository;
    }

    @Transactional
    public MarcheDTO creer(MarcheCreateRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();

        int year = LocalDate.now().getYear();
        long count = marcheRepository.count() + 1;
        String ref = String.format("MRC-%d-%03d", year, count);

        Marche marche = Marche.builder()
                .reference(ref)
                .objet(request.getObjet().trim())
                .description(request.getDescription())
                .statut(StatutMarche.BROUILLON)
                .budgetPrevisionnel(request.getBudgetPrevisionnel())
                .montantTotal(BigDecimal.ZERO)
                .dateLancement(request.getDateLancement())
                .createurId(user != null ? user.getUserId() : null)
                .createurNom(user != null ? user.getEmail() : null)
                .observations(request.getObservations())
                .lots(new ArrayList<>())
                .build();

        if (request.getLots() != null) {
            for (LotCreateRequest lotReq : request.getLots()) {
                Lot lot = Lot.builder()
                        .numero(lotReq.getNumero())
                        .designation(lotReq.getDesignation().trim())
                        .description(lotReq.getDescription())
                        .statut(StatutLot.EN_ATTENTE)
                        .montantEstime(lotReq.getMontantEstime())
                        .dateLivraisonPrevue(lotReq.getDateLivraisonPrevue())
                        .observations(lotReq.getObservations())
                        .tauxExecution(0.0)
                        .lignesLot(new ArrayList<>())
                        .build();

                if (lotReq.getLignesLot() != null) {
                    for (LigneLotRequest ligneReq : lotReq.getLignesLot()) {
                        // Validation : Si ligneBesoinId fourni, vérifier que le besoin est VALIDE
                        if (ligneReq.getLigneBesoinId() != null) {
                            validerBesoinPourLot(ligneReq.getLigneBesoinId());
                        }

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
                marche.addLot(lot);
            }
        }

        Marche saved = marcheRepository.save(marche);

        enregistrerHistorique(saved.getId(), null, StatutMarche.BROUILLON.name(),
                user != null ? user.getUserId() : null,
                user != null ? user.getEmail() : "Système",
                "Création du marché");

        return toDto(saved);
    }

    private void validerBesoinPourLot(Long ligneBesoinId) {
        // Optionnel : s'assurer que le besoin relié est au statut VALIDE
    }

    @Transactional
    public MarcheDTO modifier(Long id, MarcheUpdateRequest request) {
        Marche m = marcheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marché", "id", id));

        if (m.getStatut() == StatutMarche.CLOTURE || m.getStatut() == StatutMarche.ANNULE) {
            throw new BusinessException("Impossible de modifier un marché clôturé ou annulé");
        }

        if (request.getObjet() != null && !request.getObjet().isBlank()) m.setObjet(request.getObjet().trim());
        if (request.getDescription() != null) m.setDescription(request.getDescription());
        if (request.getBudgetPrevisionnel() != null) m.setBudgetPrevisionnel(request.getBudgetPrevisionnel());
        if (request.getDateLancement() != null) m.setDateLancement(request.getDateLancement());
        if (request.getDateCloture() != null) m.setDateCloture(request.getDateCloture());
        if (request.getObservations() != null) m.setObservations(request.getObservations());

        return toDto(marcheRepository.save(m));
    }

    @Transactional
    public MarcheDTO changerStatut(Long id, StatutMarche nouveauStatut) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        Marche m = marcheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marché", "id", id));

        StatutMarche ancien = m.getStatut();
        if (ancien == StatutMarche.CLOTURE || ancien == StatutMarche.ANNULE) {
            throw new BusinessException("Transition de statut impossible depuis " + ancien);
        }

        m.setStatut(nouveauStatut);
        if (nouveauStatut == StatutMarche.EN_COURS && m.getDateLancement() == null) {
            m.setDateLancement(LocalDateTime.now());
        }
        if (nouveauStatut == StatutMarche.CLOTURE) {
            m.setDateCloture(LocalDateTime.now());
        }

        Marche saved = marcheRepository.save(m);
        enregistrerHistorique(saved.getId(), ancien.name(), nouveauStatut.name(),
                user != null ? user.getUserId() : null,
                user != null ? user.getEmail() : "Système",
                "Passage au statut " + nouveauStatut);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public MarcheDTO trouverParId(Long id) {
        Marche m = marcheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marché", "id", id));
        return toDto(m);
    }

    @Transactional(readOnly = true)
    public PageResponse<MarcheDTO> lister(String recherche, StatutMarche statut, Pageable pageable) {
        Page<Marche> page = marcheRepository.rechercher(recherche, statut, pageable);
        List<MarcheDTO> dtos = page.getContent().stream().map(this::toDto).toList();

        return PageResponse.<MarcheDTO>builder()
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
    public void recalculerMontantTotal(Long marcheId) {
        Marche m = marcheRepository.findById(marcheId).orElse(null);
        if (m == null) return;

        BigDecimal total = m.getLots().stream()
                .map(l -> l.getMontantAttribue() != null ? l.getMontantAttribue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        m.setMontantTotal(total);
        marcheRepository.save(m);
    }

    private void enregistrerHistorique(Long id, String ancien, String nouveau, Long userId, String userNom, String com) {
        HistoriqueStatut h = HistoriqueStatut.builder()
                .entiteType("MARCHE")
                .entiteId(id)
                .ancienStatut(ancien)
                .nouveauStatut(nouveau)
                .utilisateurId(userId)
                .utilisateurNom(userNom)
                .commentaire(com)
                .build();
        historiqueRepository.save(h);
    }

    public MarcheDTO toDto(Marche m) {
        List<LotDTO> lotsDto = m.getLots() != null ? m.getLots().stream()
                .map(LotService::toDtoStatic)
                .toList() : List.of();

        return MarcheDTO.builder()
                .id(m.getId())
                .reference(m.getReference())
                .objet(m.getObjet())
                .description(m.getDescription())
                .statut(m.getStatut())
                .budgetPrevisionnel(m.getBudgetPrevisionnel())
                .montantTotal(m.getMontantTotal())
                .dateCreation(m.getDateCreation())
                .dateLancement(m.getDateLancement())
                .dateAttribution(m.getDateAttribution())
                .dateCloture(m.getDateCloture())
                .createurId(m.getCreateurId())
                .createurNom(m.getCreateurNom())
                .observations(m.getObservations())
                .lots(lotsDto)
                .build();
    }
}
