package com.ept.gestion.common.exception;

/**
 * Exception levée lorsqu'une ressource demandée n'est pas trouvée.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String ressource, String champ, Object valeur) {
        super(String.format("%s non trouvé(e) avec %s : '%s'", ressource, champ, valeur));
    }
}
