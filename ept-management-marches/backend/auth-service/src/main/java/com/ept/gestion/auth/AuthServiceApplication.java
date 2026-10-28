package com.ept.gestion.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application principale du service d'authentification et gestion des utilisateurs.
 * École Polytechnique de Thiès (EPT).
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ept.gestion.auth", "com.ept.gestion.common"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
