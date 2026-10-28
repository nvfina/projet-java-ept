package com.ept.gestion.auth.config;

import com.ept.gestion.auth.entity.ServiceDepartement;
import com.ept.gestion.auth.entity.Utilisateur;
import com.ept.gestion.auth.repository.ServiceDepartementRepository;
import com.ept.gestion.auth.repository.UtilisateurRepository;
import com.ept.gestion.common.enums.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initialisation des données de démarrage : services et comptes par défaut.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final ServiceDepartementRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           ServiceDepartementRepository serviceRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.serviceRepository = serviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initServices();
        initUtilisateurs();
    }

    private void initServices() {
        if (serviceRepository.count() == 0) {
            log.info("Initialisation des départements EPT...");
            List<ServiceDepartement> departements = List.of(
                ServiceDepartement.builder().nom("Informatique").description("Département Génie Informatique et Télécoms").actif(true).build(),
                ServiceDepartement.builder().nom("Génie Civil").description("Département Génie Civil").actif(true).build(),
                ServiceDepartement.builder().nom("Génie Électrique").description("Département Génie Électrique").actif(true).build(),
                ServiceDepartement.builder().nom("Génie Mécanique").description("Département Génie Mécanique").actif(true).build(),
                ServiceDepartement.builder().nom("Service des Achats").description("Division Passation des Marchés et Approvisionnements").actif(true).build(),
                ServiceDepartement.builder().nom("Direction").description("Direction Générale de l'EPT").actif(true).build()
            );
            serviceRepository.saveAll(departements);
            log.info("{} départements créés avec succès.", departements.size());
        }
    }

    private void initUtilisateurs() {
        if (utilisateurRepository.count() == 0) {
            log.info("Initialisation des comptes de démonstration EPT...");

            List<Utilisateur> users = List.of(
                Utilisateur.builder()
                    .nom("ADMIN")
                    .prenom("Système")
                    .email("admin@ept.sn")
                    .motDePasse(passwordEncoder.encode("Admin@2024"))
                    .telephone("+221 77 000 00 01")
                    .actif(true)
                    .role(RoleEnum.ADMIN)
                    .serviceNom("Direction")
                    .build(),

                Utilisateur.builder()
                    .nom("NIANG")
                    .prenom("Nafissatou")
                    .email("chef.info@ept.sn")
                    .motDePasse(passwordEncoder.encode("Chef@2024"))
                    .telephone("+221 77 111 22 33")
                    .actif(true)
                    .role(RoleEnum.CHEF_SERVICE)
                    .serviceNom("Informatique")
                    .build(),

                Utilisateur.builder()
                    .nom("NDIAYE")
                    .prenom("Khardiata")
                    .email("chef.civil@ept.sn")
                    .motDePasse(passwordEncoder.encode("Chef@2024"))
                    .telephone("+221 77 222 33 44")
                    .actif(true)
                    .role(RoleEnum.CHEF_SERVICE)
                    .serviceNom("Génie Civil")
                    .build(),

                Utilisateur.builder()
                    .nom("SALL")
                    .prenom("Sokhna")
                    .email("chef.elec@ept.sn")
                    .motDePasse(passwordEncoder.encode("Chef@2024"))
                    .telephone("+221 77 333 44 55")
                    .actif(true)
                    .role(RoleEnum.CHEF_SERVICE)
                    .serviceNom("Génie Électrique")
                    .build(),

                Utilisateur.builder()
                    .nom("FALL")
                    .prenom("Moussa")
                    .email("achats@ept.sn")
                    .motDePasse(passwordEncoder.encode("Achats@2024"))
                    .telephone("+221 77 444 55 66")
                    .actif(true)
                    .role(RoleEnum.SERVICE_ACHATS)
                    .serviceNom("Service des Achats")
                    .build(),

                Utilisateur.builder()
                    .nom("GUISSE")
                    .prenom("Directeur")
                    .email("direction@ept.sn")
                    .motDePasse(passwordEncoder.encode("Direction@2024"))
                    .telephone("+221 77 555 66 77")
                    .actif(true)
                    .role(RoleEnum.DIRECTION)
                    .serviceNom("Direction")
                    .build()
            );

            utilisateurRepository.saveAll(users);
            log.info("{} utilisateurs de démonstration créés avec succès.", users.size());
        }
    }
}
