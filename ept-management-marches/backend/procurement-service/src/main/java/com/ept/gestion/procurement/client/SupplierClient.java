package com.ept.gestion.procurement.client;

import com.ept.gestion.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SupplierClient {

    private static final Logger log = LoggerFactory.getLogger(SupplierClient.class);

    private final RestTemplate restTemplate;

    @Value("${supplier-service.url:http://localhost:8083}")
    private String supplierServiceUrl;

    public SupplierClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isFournisseurActif(Long fournisseurId) {
        if (fournisseurId == null) return false;
        try {
            String url = supplierServiceUrl + "/api/fournisseurs/" + fournisseurId + "/actif";
            ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Boolean>>() {}
            );
            if (response.getBody() != null && response.getBody().getDonnees() != null) {
                return Boolean.TRUE.equals(response.getBody().getDonnees());
            }
        } catch (Exception e) {
            log.warn("Impossible de contacter supplier-service à {}. On autorise en mode dégradé: {}", supplierServiceUrl, e.getMessage());
            return true; // En mode dégradé local si supplier n'est pas encore démarré
        }
        return true;
    }
}
