package com.ept.gestion.dashboard.controller;

import com.ept.gestion.dashboard.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/rapports")
@Tag(name = "Rapports", description = "Génération et exportation de rapports Excel et CSV")
public class RapportController {

    private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @GetMapping("/marches/excel")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Exporter le rapport des marchés au format Excel (.xlsx)")
    public ResponseEntity<byte[]> exporterMarchesExcel() throws IOException {
        byte[] data = rapportService.genererRapportMarchesExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-marches-ept.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/depenses/excel")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Exporter le rapport des dépenses par service au format Excel (.xlsx)")
    public ResponseEntity<byte[]> exporterDepensesExcel() throws IOException {
        byte[] data = rapportService.genererRapportDepensesExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-depenses-ept.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/csv")
    @PreAuthorize("hasRole('DIRECTION') or hasRole('SERVICE_ACHATS') or hasRole('ADMIN')")
    @Operation(summary = "Exporter un rapport au format CSV")
    public ResponseEntity<byte[]> exporterCSV(@RequestParam(defaultValue = "marches") String type) {
        byte[] data = rapportService.genererRapportCSV(type);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-ept-" + type + ".csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(data);
    }
}
