package com.ept.gestion.dashboard.service;

import com.ept.gestion.dashboard.client.ProcurementClient;
import com.ept.gestion.dashboard.client.SupplierClient;
import com.ept.gestion.dashboard.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ProcurementClient procurementClient;
    private final SupplierClient supplierClient;

    public DashboardService(ProcurementClient procurementClient, SupplierClient supplierClient) {
        this.procurementClient = procurementClient;
        this.supplierClient = supplierClient;
    }

    public DashboardDTO getIndicateurs() {
        Map<String, Object> globalStats = procurementClient.getStatistiquesGlobales();
        long totalFournisseurs = supplierClient.countFournisseurs();

        long totalDemandes = getLong(globalStats, "totalDemandes");
        long demandesEnAttente = getLong(globalStats, "demandesEnAttente");
        long demandesValidees = getLong(globalStats, "demandesValidees");
        long demandesRejetees = getLong(globalStats, "demandesRejetees");
        long demandesEnCours = getLong(globalStats, "demandesEnCours");

        long totalMarches = getLong(globalStats, "totalMarches");
        long marchesEnCours = getLong(globalStats, "marchesEnCours");
        long marchesClotures = getLong(globalStats, "marchesClotures");

        long totalLots = getLong(globalStats, "totalLots");
        long lotsAttribues = getLong(globalStats, "lotsAttribues");
        long lotsEnCoursLivraison = getLong(globalStats, "lotsEnCoursLivraison");
        long lotsClotures = getLong(globalStats, "lotsClotures");

        Double taux = globalStats.get("tauxExecutionGlobal") instanceof Number
                ? ((Number) globalStats.get("tauxExecutionGlobal")).doubleValue() : 0.0;

        BigDecimal montantDepenses = getBigDecimal(globalStats, "montantTotalDepenses");

        return DashboardDTO.builder()
                .totalDemandes(totalDemandes)
                .demandesEnAttente(demandesEnAttente)
                .demandesValidees(demandesValidees)
                .demandesRejetees(demandesRejetees)
                .demandesEnCours(demandesEnCours)
                .totalMarches(totalMarches)
                .marchesEnCours(marchesEnCours)
                .marchesClotures(marchesClotures)
                .totalLots(totalLots)
                .lotsAttribues(lotsAttribues)
                .lotsEnCoursLivraison(lotsEnCoursLivraison)
                .lotsClotures(lotsClotures)
                .tauxExecutionGlobal(taux)
                .montantTotalDepenses(montantDepenses)
                .montantTotalMarches(montantDepenses)
                .totalFournisseurs(totalFournisseurs)
                .build();
    }

    public List<DepenseServiceDTO> getDepensesParService() {
        List<Map<String, Object>> data = procurementClient.getDepensesParService();
        List<DepenseServiceDTO> result = new ArrayList<>();
        for (Map<String, Object> item : data) {
            String nom = (String) item.get("serviceNom");
            int nb = item.get("nombreDemandes") instanceof Number ? ((Number) item.get("nombreDemandes")).intValue() : 0;
            BigDecimal mt = item.get("montantTotal") instanceof Number
                    ? BigDecimal.valueOf(((Number) item.get("montantTotal")).doubleValue()) : BigDecimal.ZERO;
            result.add(new DepenseServiceDTO(nom, nb, mt));
        }
        return result;
    }

    public List<DepenseMarcheDTO> getDepensesParMarche() {
        List<Map<String, Object>> data = procurementClient.getDepensesParMarche();
        List<DepenseMarcheDTO> result = new ArrayList<>();
        for (Map<String, Object> item : data) {
            String ref = (String) item.get("marcheReference");
            String obj = (String) item.get("marcheObjet");
            String st = (String) item.get("statut");
            BigDecimal bp = item.get("budgetPrevisionnel") instanceof Number
                    ? BigDecimal.valueOf(((Number) item.get("budgetPrevisionnel")).doubleValue()) : BigDecimal.ZERO;
            BigDecimal mt = item.get("montantTotal") instanceof Number
                    ? BigDecimal.valueOf(((Number) item.get("montantTotal")).doubleValue()) : BigDecimal.ZERO;
            result.add(new DepenseMarcheDTO(ref, obj, st, bp, mt));
        }
        return result;
    }

    public List<EvolutionDTO> getEvolution() {
        return List.of(
                new EvolutionDTO("Janvier", 5, new BigDecimal("4500000")),
                new EvolutionDTO("Février", 8, new BigDecimal("7200000")),
                new EvolutionDTO("Mars", 12, new BigDecimal("12800000")),
                new EvolutionDTO("Avril", 10, new BigDecimal("9500000")),
                new EvolutionDTO("Mai", 15, new BigDecimal("18200000")),
                new EvolutionDTO("Juin", 14, new BigDecimal("16500000")),
                new EvolutionDTO("Juillet", 9, new BigDecimal("8100000")),
                new EvolutionDTO("Août", 4, new BigDecimal("3200000")),
                new EvolutionDTO("Septembre", 18, new BigDecimal("22400000")),
                new EvolutionDTO("Octobre", 22, new BigDecimal("29500000"))
        );
    }

    public List<TopFournisseurDTO> getTopFournisseurs() {
        return List.of(
                new TopFournisseurDTO("TechnoPlus SARL", 2, new BigDecimal("29500000")),
                new TopFournisseurDTO("BTP Sénégal SA", 1, new BigDecimal("12000000")),
                new TopFournisseurDTO("ElectroPower West Africa", 1, new BigDecimal("8500000")),
                new TopFournisseurDTO("FourniPro Sénégal", 1, new BigDecimal("3500000"))
        );
    }

    private long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        return 0L;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        return BigDecimal.ZERO;
    }
}
