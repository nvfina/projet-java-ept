package com.ept.gestion.procurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Point d'entrée du microservice d'approvisionnement (Procurement).
 * Gère les expressions de besoins, marchés, lots, offres tarifaires,
 * attributions et livraisons.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ept.gestion.procurement", "com.ept.gestion.common"})
public class ProcurementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcurementServiceApplication.class, args);
    }
}
