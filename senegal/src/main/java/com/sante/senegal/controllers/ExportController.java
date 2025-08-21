package com.sante.senegal.controllers;

import com.sante.senegal.services.implementations.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/patients/csv")
    public ResponseEntity<String> exporterPatientsCSV() {
        try {
            String csvContent = exportService.exporterPatientsCSV();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patients.csv");
            headers.setContentType(MediaType.TEXT_PLAIN);

            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'export CSV : " + e.getMessage());
        }
    }

    @GetMapping("/consultations/json")
    public ResponseEntity<?> exporterConsultationsJSON(
            @RequestParam("dateDebut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            String json = exportService.exporterConsultationsJSON(dateDebut, dateFin);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=consultations.json");
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(json, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'export JSON : " + e.getMessage());
        }
    }

    @GetMapping("/patients/{id}/rapport-pdf")
    public ResponseEntity<byte[]> genererRapportPDF(@PathVariable("id") Long patientId) {
        try {
            byte[] pdfData = exportService.genererRapportPDF(patientId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rapport_patient_" + patientId + ".pdf");

            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
