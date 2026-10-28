package com.ept.gestion.chatbot.nlp;

/**
 * Interface pour le moteur de traitement du langage naturel (NLP).
 * Permet de découpler la détection d'intention pour brancher
 * ultérieurement un LLM externe ou une API payante sans changer le code applicatif.
 */
public interface MoteurNLP {
    QueryResult analyser(String message);
}
