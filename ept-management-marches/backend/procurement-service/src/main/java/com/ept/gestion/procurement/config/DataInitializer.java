package com.ept.gestion.procurement.config;

import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.common.enums.StatutOffre;
import com.ept.gestion.procurement.entity.*;
import com.ept.gestion.procurement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ExpressionBesoinRepository besoinRepository;
    private final MarcheRepository marcheRepository;
    private final LotRepository lotRepository;
    private final OffreTarifaireRepository offreRepository;
    private final LivraisonRepository livraisonRepository;

    public DataInitializer(ExpressionBesoinRepository besoinRepository,
                           MarcheRepository marcheRepository,
                           LotRepository lotRepository,
                           OffreTarifaireRepository offreRepository,
                           LivraisonRepository livraisonRepository) {
        this.besoinRepository = besoinRepository;
        this.marcheRepository = marcheRepository;
        this.lotRepository = lotRepository;
        this.offreRepository = offreRepository;
        this.livraisonRepository = livraisonRepository;
    }

    @Override
    public void run(String... args) {
        if (besoinRepository.count() == 0) {
            log.info("Initialisation des données de test Procurement (Besoins, Marchés, Lots, Offres, Livraisons)...");

            // 1. Besoins
            ExpressionBesoin b1 = ExpressionBesoin.builder()
                    .reference("EB-20241001-001")
                    .serviceDemandeur("Informatique")
                    .demandeurId(2L)
                    .demandeurNom("chef.info@ept.sn")
                    .description("Renouvellement des ordinateurs portables de la salle multimédia et serveurs de calcul")
                    .motif("Machines actuelles obsolètes (plus de 6 ans d'âge)")
                    .statut(StatutBesoin.VALIDE)
                    .urgente(true)
                    .dateValidation(LocalDateTime.now().minusDays(20))
                    .validateurId(5L)
                    .validateurNom("achats@ept.sn")
                    .commentaireDecision("Demande justifiée pour les travaux pratiques des élèves ingénieurs")
                    .lignes(new ArrayList<>())
                    .build();

            b1.addLigne(LigneBesoin.builder()
                    .designation("PC Portable Développeur Core i7 32GB RAM")
                    .description("Dell Latitude ou équivalent avec carte graphique dédiée")
                    .quantiteDemandee(25)
                    .unite("unité")
                    .estimationPrix(new BigDecimal("850000"))
                    .build());

            b1.addLigne(LigneBesoin.builder()
                    .designation("Serveur de Calcul Rack 2U")
                    .description("Processeurs Xeon, 128 Go RAM, stockage NVMe 4TB")
                    .quantiteDemandee(2)
                    .unite("unité")
                    .estimationPrix(new BigDecimal("4500000"))
                    .build());

            ExpressionBesoin b2 = ExpressionBesoin.builder()
                    .reference("EB-20241010-002")
                    .serviceDemandeur("Génie Civil")
                    .demandeurId(3L)
                    .demandeurNom("chef.civil@ept.sn")
                    .description("Équipements d'essais mécaniques pour le laboratoire de géotechnique")
                    .motif("Nouveaux modules de cours et projets de recherche")
                    .statut(StatutBesoin.VALIDE)
                    .urgente(false)
                    .dateValidation(LocalDateTime.now().minusDays(15))
                    .validateurId(5L)
                    .validateurNom("achats@ept.sn")
                    .commentaireDecision("Validé conformément au budget alloué")
                    .lignes(new ArrayList<>())
                    .build();

            b2.addLigne(LigneBesoin.builder()
                    .designation("Presse hydraulique d'écrasement de béton 3000 kN")
                    .description("Affichage digital et acquisition automatisée")
                    .quantiteDemandee(1)
                    .unite("unité")
                    .estimationPrix(new BigDecimal("12000000"))
                    .build());

            ExpressionBesoin b3 = ExpressionBesoin.builder()
                    .reference("EB-20241020-003")
                    .serviceDemandeur("Génie Électrique")
                    .demandeurId(4L)
                    .demandeurNom("chef.elec@ept.sn")
                    .description("Fourniture d'appareils de mesure et bancs didactiques")
                    .motif("Amélioration des conditions d'enseignement en électrotechnique")
                    .statut(StatutBesoin.EN_ATTENTE)
                    .urgente(false)
                    .lignes(new ArrayList<>())
                    .build();

            b3.addLigne(LigneBesoin.builder()
                    .designation("Oscilloscope numérique 4 canaux 100 MHz")
                    .description("Bande passante 100MHz avec sondes")
                    .quantiteDemandee(10)
                    .unite("unité")
                    .estimationPrix(new BigDecimal("450000"))
                    .build());

            besoinRepository.saveAll(List.of(b1, b2, b3));

            // 2. Marché 1
            Marche m1 = Marche.builder()
                    .reference("MRC-2024-001")
                    .objet("Acquisition d'équipements informatiques et serveurs pour l'EPT")
                    .description("Marché passé par le Service des Achats pour équiper le département informatique")
                    .statut(StatutMarche.ATTRIBUE)
                    .budgetPrevisionnel(new BigDecimal("35000000"))
                    .montantTotal(new BigDecimal("29500000"))
                    .dateLancement(LocalDateTime.now().minusDays(18))
                    .dateAttribution(LocalDateTime.now().minusDays(10))
                    .createurId(5L)
                    .createurNom("achats@ept.sn")
                    .lots(new ArrayList<>())
                    .build();

            Lot lot1 = Lot.builder()
                    .numero(1)
                    .designation("Lot 1 : Ordinateurs portables pour étudiants et enseignants")
                    .description("25 ordinateurs portables haute performance")
                    .statut(StatutLot.EN_COURS_LIVRAISON)
                    .montantEstime(new BigDecimal("21250000"))
                    .montantAttribue(new BigDecimal("20500000"))
                    .fournisseurId(1L)
                    .fournisseurNom("TechnoPlus SARL")
                    .dateAttribution(LocalDateTime.now().minusDays(10))
                    .dateLivraisonPrevue(LocalDateTime.now().plusDays(20))
                    .tauxExecution(60.0)
                    .lignesLot(new ArrayList<>())
                    .build();

            lot1.addLigneLot(LigneLot.builder()
                    .designation("PC Portable Développeur Core i7 32GB RAM")
                    .quantite(25)
                    .prixUnitaire(new BigDecimal("820000"))
                    .montantTotal(new BigDecimal("20500000"))
                    .build());

            Lot lot2 = Lot.builder()
                    .numero(2)
                    .designation("Lot 2 : Serveurs de calcul scientifique")
                    .description("2 serveurs de calcul haute performance")
                    .statut(StatutLot.ATTRIBUE)
                    .montantEstime(new BigDecimal("9000000"))
                    .montantAttribue(new BigDecimal("9000000"))
                    .fournisseurId(1L)
                    .fournisseurNom("TechnoPlus SARL")
                    .dateAttribution(LocalDateTime.now().minusDays(10))
                    .dateLivraisonPrevue(LocalDateTime.now().plusDays(30))
                    .tauxExecution(0.0)
                    .lignesLot(new ArrayList<>())
                    .build();

            lot2.addLigneLot(LigneLot.builder()
                    .designation("Serveur de Calcul Rack 2U")
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("4500000"))
                    .montantTotal(new BigDecimal("9000000"))
                    .build());

            m1.addLot(lot1);
            m1.addLot(lot2);
            marcheRepository.save(m1);

            // 3. Offres pour lot 1
            OffreTarifaire o1 = OffreTarifaire.builder()
                    .lotId(lot1.getId())
                    .fournisseurId(1L)
                    .fournisseurNom("TechnoPlus SARL")
                    .dateOffre(LocalDateTime.now().minusDays(12))
                    .statut(StatutOffre.RETENUE)
                    .montantTotal(new BigDecimal("20500000"))
                    .devise("XOF")
                    .observations("Garantie 3 ans sur site incluse")
                    .lignesOffre(new ArrayList<>())
                    .build();

            o1.addLigneOffre(LigneOffre.builder()
                    .designation("PC Portable Développeur Core i7 32GB RAM")
                    .quantite(25)
                    .prixUnitaire(new BigDecimal("820000"))
                    .montantLigne(new BigDecimal("20500000"))
                    .build());

            OffreTarifaire o2 = OffreTarifaire.builder()
                    .lotId(lot1.getId())
                    .fournisseurId(3L)
                    .fournisseurNom("FourniPro Sénégal")
                    .dateOffre(LocalDateTime.now().minusDays(13))
                    .statut(StatutOffre.REJETEE)
                    .montantTotal(new BigDecimal("22000000"))
                    .devise("XOF")
                    .observations("Garantie 1 an constructeur")
                    .lignesOffre(new ArrayList<>())
                    .build();

            o2.addLigneOffre(LigneOffre.builder()
                    .designation("PC Portable Développeur Core i7 32GB RAM")
                    .quantite(25)
                    .prixUnitaire(new BigDecimal("880000"))
                    .montantLigne(new BigDecimal("22000000"))
                    .build());

            offreRepository.saveAll(List.of(o1, o2));

            // 4. Livraison partielle pour lot 1
            Livraison liv1 = Livraison.builder()
                    .lotId(lot1.getId())
                    .dateLivraison(LocalDateTime.now().minusDays(3))
                    .quantiteLivree(15)
                    .designation("1ère tranche : 15 ordinateurs portables")
                    .observations("Livraison conforme reçue en bon état")
                    .conforme(true)
                    .receptionnaireId(5L)
                    .receptionnaireNom("achats@ept.sn")
                    .build();

            livraisonRepository.save(liv1);

            log.info("Données Procurement créées avec succès !");
        }
    }
}
