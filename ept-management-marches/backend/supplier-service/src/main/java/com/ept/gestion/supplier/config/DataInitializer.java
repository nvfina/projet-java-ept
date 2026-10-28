package com.ept.gestion.supplier.config;

import com.ept.gestion.supplier.entity.Fournisseur;
import com.ept.gestion.supplier.repository.FournisseurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final FournisseurRepository fournisseurRepository;

    public DataInitializer(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    @Override
    public void run(String... args) {
        if (fournisseurRepository.count() == 0) {
            log.info("Initialisation des fournisseurs de démonstration EPT...");

            List<Fournisseur> fournisseurs = List.of(
                Fournisseur.builder()
                    .raisonSociale("TechnoPlus SARL")
                    .ninea("SN-DKR-2020-B-1234")
                    .adresse("Point E, Avenue Cheikh Anta Diop, Dakar")
                    .telephone("+221 33 825 00 11")
                    .email("contact@technoplus.sn")
                    .domaineIntervention("Informatique")
                    .siteWeb("https://technoplus.sn")
                    .contactPrincipal("Amadou Diallo")
                    .actif(true)
                    .observations("Spécialiste serveurs, ordinateurs et équipements réseau")
                    .build(),

                Fournisseur.builder()
                    .raisonSociale("BTP Sénégal SA")
                    .ninea("SN-THS-2019-B-5678")
                    .adresse("Zone Industrielle, Thiès")
                    .telephone("+221 33 951 22 33")
                    .email("contact@btpsenegal.sn")
                    .domaineIntervention("BTP & Travaux")
                    .siteWeb("https://btpsenegal.sn")
                    .contactPrincipal("Ibrahima Ndiaye")
                    .actif(true)
                    .observations("Matériaux de construction, équipements de laboratoire de génie civil")
                    .build(),

                Fournisseur.builder()
                    .raisonSociale("FourniPro Sénégal")
                    .ninea("SN-DKR-2021-B-9012")
                    .adresse("Mermoz, Dakar")
                    .telephone("+221 33 864 44 55")
                    .email("ventes@fournipro.sn")
                    .domaineIntervention("Fournitures de bureau")
                    .siteWeb("https://fournipro.sn")
                    .contactPrincipal("Fatou Kiné Sow")
                    .actif(true)
                    .observations("Papeterie, mobilier de bureau et consommables")
                    .build(),

                Fournisseur.builder()
                    .raisonSociale("ElectroPower West Africa")
                    .ninea("SN-DKR-2018-B-3456")
                    .adresse("Km 4 Boulevard du Centenaire, Dakar")
                    .telephone("+221 33 832 77 88")
                    .email("info@electropower.sn")
                    .domaineIntervention("Génie Électrique")
                    .siteWeb("https://electropower.sn")
                    .contactPrincipal("Ousmane Ba")
                    .actif(true)
                    .observations("Groupes électrogènes, onduleurs, matériel électrique industriel")
                    .build(),

                Fournisseur.builder()
                    .raisonSociale("MécaSolutions Industrie")
                    .ninea("SN-THS-2022-B-7890")
                    .adresse("Route de Khombole, Thiès")
                    .telephone("+221 33 952 99 00")
                    .email("service@mecasolutions.sn")
                    .domaineIntervention("Génie Mécanique")
                    .siteWeb("https://mecasolutions.sn")
                    .contactPrincipal("Cheikh Tidiane Wade")
                    .actif(true)
                    .observations("Outillage, pièces mécaniques, bancs d'essais mécaniques")
                    .build()
            );

            fournisseurRepository.saveAll(fournisseurs);
            log.info("{} fournisseurs enregistrés avec succès.", fournisseurs.size());
        }
    }
}
