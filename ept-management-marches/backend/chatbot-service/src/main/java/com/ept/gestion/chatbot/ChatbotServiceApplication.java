package com.ept.gestion.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Point d'entrée du service Chatbot.
 * Ce service fournit une interface conversationnelle
 * pour interroger le système de gestion des marchés.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ept.gestion.chatbot", "com.ept.gestion.common"})
public class ChatbotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotServiceApplication.class, args);
    }
}
