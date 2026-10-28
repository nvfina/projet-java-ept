package com.ept.gestion.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Réponse API standardisée pour tous les microservices.
 * Assure la cohérence des réponses JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean succes;
    private String message;
    private T donnees;
    private LocalDateTime horodatage;

    public static <T> ApiResponse<T> succes(T donnees) {
        return ApiResponse.<T>builder()
                .succes(true)
                .donnees(donnees)
                .horodatage(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> succes(String message, T donnees) {
        return ApiResponse.<T>builder()
                .succes(true)
                .message(message)
                .donnees(donnees)
                .horodatage(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> erreur(String message) {
        return ApiResponse.<T>builder()
                .succes(false)
                .message(message)
                .horodatage(LocalDateTime.now())
                .build();
    }
}
