package com.sante.senegal.controllers;

import com.sante.senegal.services.implementations.RapportMedicalService;
import com.sante.senegal.services.interfaces.AuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/rapports")
@CrossOrigin(origins = "*")
@Validated
@RequiredArgsConstructor
public class RapportController {

    private final RapportMedicalService rapportMedicalService;
    private final AuditService auditService;

    /**
     * Générer un rapport de consultation
     */
    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<Map<String, Object>> genererRapportConsultation(
            @PathVariable @NotNull Long consultationId,
            Authentication authentication) {

        try {
            Map<String, Object> rapport = rapportMedicalService.genererRapportConsultation(consultationId);

//            auditService.enregistrerAction("GENERATION_RAPPORT_CONSULTATION", "CONSULTATION",
//                    consultationId, authentication.getName(), "Rapport généré");

            return ResponseEntity.ok(rapport);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer un rapport de suivi patient
     */
    @GetMapping("/patient/{patientId}/suivi")
    public ResponseEntity<Map<String, Object>> genererRapportSuiviPatient(
            @PathVariable @NotNull Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Authentication authentication) {

        try {
            if (dateDebut.isAfter(dateFin)) {
                throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
            }

            Map<String, Object> rapport = rapportMedicalService.genererRapportSuiviPatient(
                    patientId, dateDebut, dateFin);

            String details = String.format("Patient ID: %d, Période: %s à %s",
                    patientId, dateDebut, dateFin);
//            auditService.enregistrerAction("GENERATION_RAPPORT_SUIVI", "PATIENT",
//                    patientId, authentication.getName(), details);

            return ResponseEntity.ok(rapport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer des statistiques médicales
     */
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> genererStatistiquesMedicales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Authentication authentication) {

        try {
            if (dateDebut.isAfter(dateFin)) {
                throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
            }
            if (dateDebut.plusYears(1).isBefore(dateFin)) {
                throw new IllegalArgumentException("La période ne peut pas dépasser 1 an");
            }

            Map<String, Object> statistiques = rapportMedicalService.genererStatistiquesMedicales(
                    dateDebut, dateFin);

//            String details = String.format("Période: %s à %s", dateDebut, dateFin);
//            auditService.enregistrerAction("GENERATION_STATISTIQUES", "SYSTEM", null,
//                    authentication.getName(), details);

            return ResponseEntity.ok(statistiques);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Supprimer les endpoints non implémentés dans le service :
     * - genererRapportPersonnalise()
     * - obtenirHistoriqueRapports()
     */
}