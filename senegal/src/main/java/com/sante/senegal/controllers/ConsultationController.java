package com.sante.senegal.controllers;

import com.sante.senegal.dto.ConsultationDto;
import com.sante.senegal.dto.CreateConsultationRequest;
import com.sante.senegal.dto.UpdateConsultationRequest;
import com.sante.senegal.services.interfaces.ConsultationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consultations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<ConsultationDto> creerConsultation(@RequestBody CreateConsultationRequest request) {
        try {
            ConsultationDto consultation = consultationService.creerConsultation(
                    request.getRendezVousId(),
                    request.getSymptomes(),
                    request.getDiagnostic(),
                    request.getObservations(),
                    request.getRecommandations()
            );
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDto> mettreAJourConsultation(
            @PathVariable Long id,
            @RequestBody UpdateConsultationRequest request) {
        try {
            ConsultationDto dto = new ConsultationDto();
            dto.setSymptomes(request.getSymptomes());
            dto.setDiagnostic(request.getDiagnostic());
            dto.setObservations(request.getObservations());
            dto.setRecommandations(request.getRecommandations());
            dto.setStatut(request.getStatutConsultation());
            dto.setSatisfaction(request.getSatisfaction());

            ConsultationDto updated = consultationService.mettreAJourConsultation(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<ConsultationDto> terminerConsultation(
            @PathVariable Long id,
            @RequestParam Integer dureeReelle) {
        try {
            ConsultationDto consultation = consultationService.terminerConsultation(id, dureeReelle);
            return ResponseEntity.ok(consultation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}/historique")
    public ResponseEntity<List<ConsultationDto>> getHistoriqueConsultations(
            @PathVariable Long patientId) {
        List<ConsultationDto> consultations = consultationService.getHistoriqueConsultations(patientId);
        return ResponseEntity.ok(consultations);
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour accepter les formats datetime d'Angular
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<ConsultationDto>> getConsultationsMedecin(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        // Convertir LocalDateTime vers LocalDate pour l'appel au service
        LocalDate dateDebutLocal = dateDebut.toLocalDate();
        LocalDate dateFinLocal = dateFin.toLocalDate();

        List<ConsultationDto> consultations = consultationService.getConsultationsMedecin(medecinId, dateDebutLocal, dateFinLocal);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDto> getConsultation(@PathVariable Long id) {
        try {
            ConsultationDto consultation = consultationService.getConsultationById(id);
            return ResponseEntity.ok(consultation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}