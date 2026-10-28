package com.ept.gestion.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Point d'entrée du microservice de gestion des fournisseurs.
 * <p>
 * Ce service gère le référentiel des fournisseurs du système EPT :
 * création, modification, recherche, activation/désactivation.
 * </p>
 *
 * @author EPT - Gestion des Marchés
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.ept.gestion.supplier",
        "com.ept.gestion.common"
})
public class SupplierServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplierServiceApplication.class, args);
    }
}
