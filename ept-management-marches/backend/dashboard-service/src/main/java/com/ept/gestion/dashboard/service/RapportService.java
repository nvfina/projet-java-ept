package com.ept.gestion.dashboard.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class RapportService {

    private final DashboardService dashboardService;

    public RapportService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public byte[] genererRapportMarchesExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Marchés EPT");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Référence", "Objet", "Statut", "Budget Prévisionnel (FCFA)", "Montant Total (FCFA)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            var depenses = dashboardService.getDepensesParMarche();
            int rowIdx = 1;
            for (var m : depenses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getMarcheReference());
                row.createCell(1).setCellValue(m.getMarcheObjet());
                row.createCell(2).setCellValue(m.getStatut());
                row.createCell(3).setCellValue(m.getBudgetPrevisionnel().doubleValue());
                row.createCell(4).setCellValue(m.getMontantTotal().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] genererRapportDepensesExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Dépenses par Service");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Service / Département", "Nombre de Demandes", "Montant Total Engagé (FCFA)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            var depenses = dashboardService.getDepensesParService();
            int rowIdx = 1;
            for (var d : depenses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getServiceNom());
                row.createCell(1).setCellValue(d.getNombreDemandes());
                row.createCell(2).setCellValue(d.getMontantTotal().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] genererRapportCSV(String type) {
        StringBuilder sb = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        if ("marches".equalsIgnoreCase(type)) {
            sb.append("Rapport des Marches EPT - ").append(dateStr).append("\n");
            sb.append("Reference;Objet;Statut;Budget Previsionnel;Montant Total\n");
            for (var m : dashboardService.getDepensesParMarche()) {
                sb.append(m.getMarcheReference()).append(";")
                  .append(m.getMarcheObjet().replace(";", " ")).append(";")
                  .append(m.getStatut()).append(";")
                  .append(m.getBudgetPrevisionnel()).append(";")
                  .append(m.getMontantTotal()).append("\n");
            }
        } else {
            sb.append("Rapport des Depenses EPT par Service - ").append(dateStr).append("\n");
            sb.append("Service;Nombre Demandes;Montant Total\n");
            for (var d : dashboardService.getDepensesParService()) {
                sb.append(d.getServiceNom()).append(";")
                  .append(d.getNombreDemandes()).append(";")
                  .append(d.getMontantTotal()).append("\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
