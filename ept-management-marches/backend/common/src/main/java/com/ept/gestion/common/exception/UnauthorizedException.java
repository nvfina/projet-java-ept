package com.ept.gestion.common.exception;

/**
 * Exception levée lorsqu'un accès non autorisé est tenté.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Accès non autorisé");
    }
}
