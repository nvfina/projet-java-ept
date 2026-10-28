package com.ept.gestion.common.exception;

/**
 * Exception levée lorsqu'une règle métier est violée.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
