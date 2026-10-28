package com.ept.gestion.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Point d'entrée du service Dashboard.
 * Ce service agrège les données des autres microservices
 * pour fournir des indicateurs et des rapports.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ept.gestion.dashboard", "com.ept.gestion.common"})
public class DashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardServiceApplication.class, args);
    }
}
