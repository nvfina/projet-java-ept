package com.ept.gestion.procurement.service;

import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.procurement.entity.ExpressionBesoin;
import com.ept.gestion.procurement.entity.Lot;
import com.ept.gestion.procurement.entity.Marche;
import com.ept.gestion.procurement.repository.ExpressionBesoinRepository;
import com.ept.gestion.procurement.repository.LotRepository;
import com.ept.gestion.procurement.repository.MarcheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class StatistiqueService {

    private final ExpressionBesoinRepository besoinRepository;
    private final MarcheRepository marcheRepository;
    private final LotRepository lotRepository;

    public StatistiqueService(ExpressionBesoinRepository besoinRepository,
                              MarcheRepository marcheRepository,
                              LotRepository lotRepository) {
        this.besoinRepository = besoinRepository;
        this.marcheRepository = marcheRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiquesGlobales() {
        Map<String, Object> stats = new HashMap<>();

        // Besoins
        long totalBesoins = besoinRepository.count();
        long besoinsEnAttente = besoinRepository.countByStatut(StatutBesoin.EN_ATTENTE);
        long besoinsValides = besoinRepository.countByStatut(StatutBesoin.VALIDE);
        long besoinsRejetes = besoinRepository.countByStatut(StatutBesoin.REJETE);
        long besoinsEnCours = besoinRepository.countByStatut(StatutBesoin.EN_COURS_ACQUISITION);

        stats.put("totalDemandes", totalBesoins);
        stats.put("demandesEnAttente", besoinsEnAttente);
        stats.put("demandesValidees", besoinsValides);
        stats.put("demandesRejetees", besoinsRejetes);
        stats.put("demandesEnCours", besoinsEnCours);

        // Marchés
        long totalMarches = marcheRepository.count();
        long marchesEnCours = marcheRepository.countByStatut(StatutMarche.EN_COURS) +
                              marcheRepository.countByStatut(StatutMarche.ATTRIBUE) +
                              marcheRepository.countByStatut(StatutMarche.EN_EXECUTION);
        long marchesClotures = marcheRepository.countByStatut(StatutMarche.CLOTURE);

        stats.put("totalMarches", totalMarches);
        stats.put("marchesEnCours", marchesEnCours);
        stats.put("marchesClotures", marchesClotures);

        // Lots
        List<Lot> lots = lotRepository.findAll();
        long totalLots = lots.size();
        long lotsAttribues = lots.stream().filter(l -> l.getStatut() == StatutLot.ATTRIBUE).count();
        long lotsEnCoursLivraison = lots.stream().filter(l -> l.getStatut() == StatutLot.EN_COURS_LIVRAISON || l.getStatut() == StatutLot.LIVRE_PARTIELLEMENT).count();
        long lotsClotures = lots.stream().filter(l -> l.getStatut() == StatutLot.LIVRE || l.getStatut() == StatutLot.CLOTURE).count();

        stats.put("totalLots", totalLots);
        stats.put("lotsAttribues", lotsAttribues);
        stats.put("lotsEnCoursLivraison", lotsEnCoursLivraison);
        stats.put("lotsClotures", lotsClotures);

        // Taux moyen d'exécution global
        double tauxGlobal = lots.isEmpty() ? 0.0 :
                lots.stream().mapToDouble(l -> l.getTauxExecution() != null ? l.getTauxExecution() : 0.0).average().orElse(0.0);
        stats.put("tauxExecutionGlobal", Math.round(tauxGlobal * 100.0) / 100.0);

        // Montant total dépensé / engagé
        BigDecimal montantTotalEngage = lots.stream()
                .map(l -> l.getMontantAttribue() != null ? l.getMontantAttribue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("montantTotalDepenses", montantTotalEngage);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDepensesParService() {
        List<ExpressionBesoin> besoins = besoinRepository.findAll();
        Map<String, Map<String, Object>> map = new HashMap<>();

        for (ExpressionBesoin b : besoins) {
            String sNom = b.getServiceDemandeur();
            map.putIfAbsent(sNom, new HashMap<>(Map.of(
                    "serviceNom", sNom,
                    "nombreDemandes", 0,
                    "montantTotal", BigDecimal.ZERO
            )));

            Map<String, Object> entry = map.get(sNom);
            entry.put("nombreDemandes", (int) entry.get("nombreDemandes") + 1);

            // Additionner l'estimation des lignes
            BigDecimal totalB = b.getLignes().stream()
                    .map(l -> {
                        BigDecimal p = l.getEstimationPrix() != null ? l.getEstimationPrix() : BigDecimal.ZERO;
                        return p.multiply(BigDecimal.valueOf(l.getQuantiteDemandee()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentM = (BigDecimal) entry.get("montantTotal");
            entry.put("montantTotal", currentM.add(totalB));
        }

        return new ArrayList<>(map.values());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDepensesParMarche() {
        List<Marche> marches = marcheRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Marche m : marches) {
            result.add(Map.of(
                    "marcheReference", m.getReference(),
                    "marcheObjet", m.getObjet(),
                    "statut", m.getStatut().name(),
                    "budgetPrevisionnel", m.getBudgetPrevisionnel() != null ? m.getBudgetPrevisionnel() : BigDecimal.ZERO,
                    "montantTotal", m.getMontantTotal() != null ? m.getMontantTotal() : BigDecimal.ZERO
            ));
        }

        return result;
    }
}
