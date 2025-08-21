package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.services.interfaces.PatientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final ModelMapper modelMapper;

    // ===============================
    // Inscription d’un patient
    // ===============================
    @PostMapping("/public/patient/inscription")
    public ResponseEntity<?> inscrirePatient(@Valid @RequestBody InscriptionPatientDto dto) {
        try {
            log.info("Réception demande inscription patient: {}", dto.getEmail());
            AuthResponseDto response = patientService.inscrirePatient(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de l'inscription patient: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'inscription patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    // ===============================
    // Consultation profil par email
    // ===============================
    @GetMapping("/patient/profil")
    public ResponseEntity<UtilisateurDetailDto> consulterProfil(@RequestParam String email) {
        UtilisateurDetailDto profil = patientService.consulterProfil(email);
        return ResponseEntity.ok(profil);
    }

    // ===============================
    // Modifier profil
    // ===============================
    @PutMapping("/patient/{id}/modifier-profil")
    public ResponseEntity<AuthResponseDto> modifierProfil(
            @PathVariable Long id,
            @RequestBody @Valid ModificationProfilPatientDto dto
    ) {
        AuthResponseDto response = patientService.modifierProfil(id, dto);
        return ResponseEntity.ok(response);
    }

    // ===============================
    // Changer mot de passe
    // ===============================
    @PutMapping("/patient/{id}/changer-mot-de-passe")
    public ResponseEntity<?> changerMotDePasse(
            @PathVariable Long id,
            @RequestBody ChangementMotDePasseDto dto
    ) {
        try {
            patientService.changerMotDePasse(id, dto);
            return ResponseEntity.ok().build();
        } catch (InscriptionException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        }
    }

    // ===============================
    // Récupérer tous les patients
    // ===============================
    @GetMapping("/patient/all")
    public ResponseEntity<List<PatientDto>> getPatients() {
        try {
            List<Patient> patients = patientService.getPatients();  // Cette méthode doit exister dans PatientService

            List<PatientDto> dtos = patients.stream().map(patient -> {
                PatientDto dto = modelMapper.map(patient, PatientDto.class);
                if (patient.getDossierMedical() != null) {
                    dto.setIdDossierMedical(patient.getDossierMedical().getIdDossier());
                }
                return dto;
            }).toList();

            return ResponseEntity.ok(dtos);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===============================
    // Récupérer un patient par ID
    // ===============================
    @GetMapping("/patient/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        try {
            Patient patient = patientService.getPatientById(id);
            PatientDto dto = modelMapper.map(patient, PatientDto.class);
            dto.setIdDossierMedical(
                    patient.getDossierMedical() != null ? patient.getDossierMedical().getIdDossier() : null
            );
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===============================
    // Vérifier si un email existe
    // ===============================
    @GetMapping("/public/patient/email-existe")
    public ResponseEntity<Boolean> emailExiste(@RequestParam String email) {
        boolean exists = patientService.emailExiste(email);
        return ResponseEntity.ok(exists);
    }

    // ===============================
    // Helper
    // ===============================
    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> erreur = new HashMap<>();
        erreur.put("erreur", message);
        return erreur;
    }
}
