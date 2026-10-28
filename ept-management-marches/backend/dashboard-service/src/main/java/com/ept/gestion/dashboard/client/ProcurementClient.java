package com.ept.gestion.dashboard.client;

import com.ept.gestion.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ProcurementClient {

    private static final Logger log = LoggerFactory.getLogger(ProcurementClient.class);
    private final RestTemplate restTemplate;

    @Value("${services.procurement-url:http://localhost:8082}")
    private String procurementUrl;

    public ProcurementClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String token = attrs.getRequest().getHeader("Authorization");
            if (token != null) {
                headers.set("Authorization", token);
            }
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getStatistiquesGlobales() {
        try {
            String url = procurementUrl + "/api/statistiques/global";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<Map<String, Object>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur d'appel vers procurement-service pour les statistiques : {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    public List<Map<String, Object>> getDepensesParService() {
        try {
            String url = procurementUrl + "/api/statistiques/depenses/service";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur d'appel vers procurement-service pour depenses/service : {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Map<String, Object>> getDepensesParMarche() {
        try {
            String url = procurementUrl + "/api/statistiques/depenses/marche";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur d'appel vers procurement-service pour depenses/marche : {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
