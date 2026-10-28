package com.ept.gestion.chatbot.client;

import com.ept.gestion.common.dto.ApiResponse;
import com.ept.gestion.common.dto.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public List<Map<String, Object>> getMarches() {
        try {
            String url = procurementUrl + "/api/marches?size=20";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<PageResponse<Map<String, Object>>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees().getContenu();
            }
        } catch (Exception e) {
            log.warn("Erreur chatbot -> procurement marches : {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Map<String, Object>> getDemandesEnAttente() {
        try {
            String url = procurementUrl + "/api/besoins?statut=EN_ATTENTE&size=20";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<PageResponse<Map<String, Object>>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<PageResponse<Map<String, Object>>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees().getContenu();
            }
        } catch (Exception e) {
            log.warn("Erreur chatbot -> procurement besoins : {}", e.getMessage());
        }
        return Collections.emptyList();
    }

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
            log.warn("Erreur chatbot -> procurement stats : {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    public Map<String, Object> getLot(Long lotId) {
        try {
            String url = procurementUrl + "/api/lots/" + lotId;
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<Map<String, Object>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur chatbot -> procurement lot {}: {}", lotId, e.getMessage());
        }
        return null;
    }
}
