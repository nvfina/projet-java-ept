package com.ept.gestion.dashboard.client;

import com.ept.gestion.common.dto.ApiResponse;
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
public class SupplierClient {

    private static final Logger log = LoggerFactory.getLogger(SupplierClient.class);
    private final RestTemplate restTemplate;

    @Value("${services.supplier-url:http://localhost:8083}")
    private String supplierUrl;

    public SupplierClient(RestTemplate restTemplate) {
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

    public long countFournisseurs() {
        try {
            String url = supplierUrl + "/api/fournisseurs/stats/count";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<Long>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<Long>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur d'appel vers supplier-service count : {}", e.getMessage());
        }
        return 0L;
    }

    public List<Map<String, Object>> getFournisseursActifs() {
        try {
            String url = supplierUrl + "/api/fournisseurs/actifs";
            HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
            ResponseEntity<ApiResponse<List<Map<String, Object>>>> res = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {}
            );
            if (res.getBody() != null && res.getBody().getDonnees() != null) {
                return res.getBody().getDonnees();
            }
        } catch (Exception e) {
            log.warn("Erreur d'appel vers supplier-service actifs : {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
