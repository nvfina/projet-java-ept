package com.ept.gestion.common.enums;

/**
 * Statuts d'une expression de besoin.
 * Transitions autorisées :
 * EN_ATTENTE -> VALIDE | REJETE
 * VALIDE -> EN_COURS_ACQUISITION
 * EN_COURS_ACQUISITION -> CLOTURE
 */
public enum StatutBesoin {
    EN_ATTENTE,
    VALIDE,
    REJETE,
    EN_COURS_ACQUISITION,
    CLOTURE
}
