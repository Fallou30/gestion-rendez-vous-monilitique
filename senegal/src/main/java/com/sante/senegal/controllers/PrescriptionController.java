package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.MedicamentPrescrit;
import com.sante.senegal.services.interfaces.PrescriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody CreatePrescriptionRequest request) {
        try {
            PrescriptionDto prescription = prescriptionService.creerPrescription(
                    request.getConsultationId(),
                    request.getDureeTraitement(),
                    request.getInstructionsGenerales()
            );
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{prescriptionId}/medicaments")
    public ResponseEntity<MedicamentPrescritDto> addMedication(
            @PathVariable Long prescriptionId,
            @RequestBody CreateMedicamentRequest request) {
        try {
            MedicamentPrescritDto medication = prescriptionService.ajouterMedicament(
                    prescriptionId,
                    request.getNomMedicament(),
                    request.getDosage(),
                    request.getFrequence(),
                    request.getDuree(),
                    request.getInstructionsSpecifiques(),
                    request.getQuantitePrescrite()
            );
            return ResponseEntity.ok(medication);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/medicaments/{medicamentId}/status")
    public ResponseEntity<MedicamentPrescritDto> updateMedicationStatus(
            @PathVariable Long medicamentId,
            @RequestParam String status) {
        try {
            MedicamentPrescritDto medication = prescriptionService.mettreAJourStatutMedicament(
                    medicamentId,
                    MedicamentPrescrit.StatutMedicament.valueOf(status)
            );
            return ResponseEntity.ok(medication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<PrescriptionDto> completePrescription(@PathVariable Long id) {
        try {
            PrescriptionDto prescription = prescriptionService.terminerPrescription(id);
            return ResponseEntity.ok(prescription);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<PrescriptionDto>> getActivePrescriptions(@PathVariable Long patientId) {
        List<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsActives(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDto> getPrescription(@PathVariable Long id) {
        try {
            PrescriptionDto prescription = prescriptionService.getPrescriptionById(id);
            return ResponseEntity.ok(prescription);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}