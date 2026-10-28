package com.ept.gestion.procurement.service;

import com.ept.gestion.common.dto.PageResponse;
import com.ept.gestion.common.enums.RoleEnum;
import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.common.exception.BusinessException;
import com.ept.gestion.common.exception.ResourceNotFoundException;
import com.ept.gestion.common.exception.UnauthorizedException;
import com.ept.gestion.common.security.JwtUserDetails;
import com.ept.gestion.common.security.SecurityUtils;
import com.ept.gestion.procurement.dto.*;
import com.ept.gestion.procurement.entity.ExpressionBesoin;
import com.ept.gestion.procurement.entity.HistoriqueStatut;
import com.ept.gestion.procurement.entity.LigneBesoin;
import com.ept.gestion.procurement.repository.ExpressionBesoinRepository;
import com.ept.gestion.procurement.repository.HistoriqueStatutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BesoinService {

    private final ExpressionBesoinRepository besoinRepository;
    private final HistoriqueStatutRepository historiqueRepository;

    public BesoinService(ExpressionBesoinRepository besoinRepository,
                         HistoriqueStatutRepository historiqueRepository) {
        this.besoinRepository = besoinRepository;
        this.historiqueRepository = historiqueRepository;
    }

    @Transactional
    public BesoinDTO creer(BesoinCreateRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("Utilisateur non authentifié");
        }

        // Seul CHEF_SERVICE ou ADMIN peut initier une expression de besoin
        if (!user.hasRole("CHEF_SERVICE") && !user.hasRole("ADMIN")) {
            throw new UnauthorizedException("Seul un chef de service peut émettre une expression de besoin");
        }

        long count = besoinRepository.count() + 1;
        String dateStr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String ref = String.format("EB-%s-%03d", dateStr, count);

        ExpressionBesoin besoin = ExpressionBesoin.builder()
                .reference(ref)
                .serviceDemandeur(user.getService() != null ? user.getService() : "Non spécifié")
                .demandeurId(user.getUserId())
                .demandeurNom(user.getEmail())
                .description(request.getDescription().trim())
                .motif(request.getMotif())
                .statut(StatutBesoin.EN_ATTENTE)
                .urgente(request.isUrgente())
                .lignes(new ArrayList<>())
                .build();

        if (request.getLignes() != null) {
            for (LigneBesoinRequest lReq : request.getLignes()) {
                LigneBesoin l = LigneBesoin.builder()
                        .designation(lReq.getDesignation().trim())
                        .description(lReq.getDescription())
                        .quantiteDemandee(lReq.getQuantiteDemandee())
                        .unite(lReq.getUnite() != null ? lReq.getUnite() : "unité")
                        .estimationPrix(lReq.getEstimationPrix())
                        .observations(lReq.getObservations())
                        .build();
                besoin.addLigne(l);
            }
        }

        ExpressionBesoin saved = besoinRepository.save(besoin);

        // Tracer dans l'historique
        enregistrerHistorique(saved.getId(), null, StatutBesoin.EN_ATTENTE.name(),
                user.getUserId(), user.getEmail(), "Création de l'expression de besoin");

        return toDto(saved);
    }

    @Transactional
    public BesoinDTO modifier(Long id, BesoinUpdateRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        ExpressionBesoin besoin = besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression de besoin", "id", id));

        // Règle métier : On ne peut modifier que si le besoin est EN_ATTENTE
        if (besoin.getStatut() != StatutBesoin.EN_ATTENTE) {
            throw new BusinessException("Impossible de modifier une demande déjà traitée (Statut actuel : " + besoin.getStatut() + ")");
        }

        // Règle métier : Seul le propriétaire ou un ADMIN peut modifier
        if (user != null && !user.hasRole("ADMIN") && !besoin.getDemandeurId().equals(user.getUserId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette demande");
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            besoin.setDescription(request.getDescription().trim());
        }
        if (request.getMotif() != null) {
            besoin.setMotif(request.getMotif());
        }
        if (request.getUrgente() != null) {
            besoin.setUrgente(request.getUrgente());
        }

        if (request.getLignes() != null) {
            besoin.getLignes().clear();
            for (LigneBesoinRequest lReq : request.getLignes()) {
                LigneBesoin l = LigneBesoin.builder()
                        .designation(lReq.getDesignation().trim())
                        .description(lReq.getDescription())
                        .quantiteDemandee(lReq.getQuantiteDemandee())
                        .unite(lReq.getUnite() != null ? lReq.getUnite() : "unité")
                        .estimationPrix(lReq.getEstimationPrix())
                        .observations(lReq.getObservations())
                        .build();
                besoin.addLigne(l);
            }
        }

        return toDto(besoinRepository.save(besoin));
    }

    @Transactional
    public void annuler(Long id) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        ExpressionBesoin besoin = besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression de besoin", "id", id));

        if (besoin.getStatut() != StatutBesoin.EN_ATTENTE) {
            throw new BusinessException("Impossible d'annuler une demande déjà traitée (Statut : " + besoin.getStatut() + ")");
        }

        if (user != null && !user.hasRole("ADMIN") && !besoin.getDemandeurId().equals(user.getUserId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à annuler cette demande");
        }

        besoinRepository.delete(besoin);
    }

    @Transactional
    public BesoinDTO valider(Long id, ValidationBesoinRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        ExpressionBesoin besoin = besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression de besoin", "id", id));

        if (besoin.getStatut() != StatutBesoin.EN_ATTENTE) {
            throw new BusinessException("Seule une demande EN_ATTENTE peut être validée. Statut actuel : " + besoin.getStatut());
        }

        String ancienStatut = besoin.getStatut().name();
        besoin.setStatut(StatutBesoin.VALIDE);
        besoin.setDateValidation(LocalDateTime.now());
        if (user != null) {
            besoin.setValidateurId(user.getUserId());
            besoin.setValidateurNom(user.getEmail());
        }
        besoin.setCommentaireDecision(request != null ? request.getCommentaire() : null);

        ExpressionBesoin saved = besoinRepository.save(besoin);
        enregistrerHistorique(saved.getId(), ancienStatut, StatutBesoin.VALIDE.name(),
                user != null ? user.getUserId() : null,
                user != null ? user.getEmail() : "Système",
                request != null ? request.getCommentaire() : "Demande validée par le Service des Achats");

        return toDto(saved);
    }

    @Transactional
    public BesoinDTO rejeter(Long id, ValidationBesoinRequest request) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        ExpressionBesoin besoin = besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression de besoin", "id", id));

        if (besoin.getStatut() != StatutBesoin.EN_ATTENTE) {
            throw new BusinessException("Seule une demande EN_ATTENTE peut être rejetée. Statut actuel : " + besoin.getStatut());
        }

        String ancienStatut = besoin.getStatut().name();
        besoin.setStatut(StatutBesoin.REJETE);
        besoin.setDateValidation(LocalDateTime.now());
        if (user != null) {
            besoin.setValidateurId(user.getUserId());
            besoin.setValidateurNom(user.getEmail());
        }
        besoin.setCommentaireDecision(request != null ? request.getCommentaire() : null);

        ExpressionBesoin saved = besoinRepository.save(besoin);
        enregistrerHistorique(saved.getId(), ancienStatut, StatutBesoin.REJETE.name(),
                user != null ? user.getUserId() : null,
                user != null ? user.getEmail() : "Système",
                request != null ? request.getCommentaire() : "Demande rejetée par le Service des Achats");

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public BesoinDTO trouverParId(Long id) {
        ExpressionBesoin besoin = besoinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression de besoin", "id", id));

        // Vérification des droits d'accès
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        if (user != null && user.hasRole("CHEF_SERVICE") && !user.hasRole("SERVICE_ACHATS") && !user.hasRole("DIRECTION") && !user.hasRole("ADMIN")) {
            if (!besoin.getDemandeurId().equals(user.getUserId())) {
                throw new UnauthorizedException("Vous n'avez pas accès aux demandes d'un autre service");
            }
        }

        return toDto(besoin);
    }

    @Transactional(readOnly = true)
    public PageResponse<BesoinDTO> lister(String service, StatutBesoin statut, Pageable pageable) {
        JwtUserDetails user = SecurityUtils.getCurrentUser();
        Long filterUserId = null;

        // Si l'utilisateur est un chef de service simple, il ne voit que ses demandes
        if (user != null && user.hasRole("CHEF_SERVICE") && !user.hasRole("SERVICE_ACHATS") && !user.hasRole("DIRECTION") && !user.hasRole("ADMIN")) {
            filterUserId = user.getUserId();
        }

        Page<ExpressionBesoin> page = besoinRepository.rechercher(service, statut, filterUserId, pageable);
        List<BesoinDTO> dtos = page.getContent().stream().map(this::toDto).toList();

        return PageResponse.<BesoinDTO>builder()
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
    public PageResponse<BesoinDTO> listerMesBesoins(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }
        Page<ExpressionBesoin> page = besoinRepository.findByDemandeurId(userId, pageable);
        List<BesoinDTO> dtos = page.getContent().stream().map(this::toDto).toList();
        return PageResponse.<BesoinDTO>builder()
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
    public List<BesoinDTO> listerBesoinsValides() {
        return besoinRepository.findByStatut(StatutBesoin.VALIDE).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoriqueStatutDTO> getHistorique(Long besoinId) {
        return historiqueRepository.findByEntiteTypeAndEntiteIdOrderByDateChangementDesc("BESOIN", besoinId)
                .stream()
                .map(h -> HistoriqueStatutDTO.builder()
                        .id(h.getId())
                        .entiteType(h.getEntiteType())
                        .entiteId(h.getEntiteId())
                        .ancienStatut(h.getAncienStatut())
                        .nouveauStatut(h.getNouveauStatut())
                        .dateChangement(h.getDateChangement())
                        .utilisateurId(h.getUtilisateurId())
                        .utilisateurNom(h.getUtilisateurNom())
                        .commentaire(h.getCommentaire())
                        .build())
                .toList();
    }

    private void enregistrerHistorique(Long id, String ancien, String nouveau, Long userId, String userNom, String com) {
        HistoriqueStatut h = HistoriqueStatut.builder()
                .entiteType("BESOIN")
                .entiteId(id)
                .ancienStatut(ancien)
                .nouveauStatut(nouveau)
                .utilisateurId(userId)
                .utilisateurNom(userNom)
                .commentaire(com)
                .build();
        historiqueRepository.save(h);
    }

    public BesoinDTO toDto(ExpressionBesoin b) {
        List<LigneBesoinDTO> lignesDto = b.getLignes() != null ? b.getLignes().stream()
                .map(l -> LigneBesoinDTO.builder()
                        .id(l.getId())
                        .designation(l.getDesignation())
                        .description(l.getDescription())
                        .quantiteDemandee(l.getQuantiteDemandee())
                        .unite(l.getUnite())
                        .estimationPrix(l.getEstimationPrix())
                        .observations(l.getObservations())
                        .build())
                .toList() : List.of();

        return BesoinDTO.builder()
                .id(b.getId())
                .reference(b.getReference())
                .serviceDemandeur(b.getServiceDemandeur())
                .demandeurId(b.getDemandeurId())
                .demandeurNom(b.getDemandeurNom())
                .description(b.getDescription())
                .motif(b.getMotif())
                .statut(b.getStatut())
                .dateCreation(b.getDateCreation())
                .dateModification(b.getDateModification())
                .dateValidation(b.getDateValidation())
                .validateurId(b.getValidateurId())
                .validateurNom(b.getValidateurNom())
                .commentaireDecision(b.getCommentaireDecision())
                .urgente(b.isUrgente())
                .lignes(lignesDto)
                .build();
    }
}
