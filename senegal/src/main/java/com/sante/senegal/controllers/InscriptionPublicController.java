/*
package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.exceptions.InscriptionException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InscriptionPublicController {

    private final InscriptionService inscriptionService;


    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        try {
            Patient patient = inscriptionService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/medecin/{id}")
    public ResponseEntity<Medecin> getMedecinById(@PathVariable Long id) {
        try {
            Medecin medecin = inscriptionService.getMedecinById(id);
            return ResponseEntity.ok(medecin);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getPatients() {
        try {
            List <Patient> patients = inscriptionService.getPatients();
            return ResponseEntity.ok(patients);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/public/patient/inscription")
    public ResponseEntity<?> inscrirePatient(@Valid @RequestBody InscriptionPatientDto dto) {
        try {
            log.info("Réception demande inscription patient: {}", dto.getEmail());
            AuthResponseDto response = inscriptionService.inscrirePatient(dto);
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

    @PostMapping("/medecin/demande")
    public ResponseEntity<?> demandeInscriptionMedecin(
            @ModelAttribute @Valid DemandeMedecinDto dto,
            @RequestParam(required = false) MultipartFile cv,
            @RequestParam(required = false) MultipartFile diplome,
            @RequestParam(required = false) MultipartFile carteOrdre
    ) {
        try {
            log.info("Création médecin par admin: {}", dto.getEmail());
            AuthResponseDto response = inscriptionService.demanderInscriptionMedecin(dto, cv, diplome, carteOrdre);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la création du médecin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du médecin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PostMapping("/admin/valider-demande/{idMedecin}")
    public ResponseEntity<?> validerDemandeInscription(
            @PathVariable Long idMedecin,
            @RequestParam boolean approuver,
            @RequestParam(required = false) String commentaire) {
        try {
            AuthResponseDto response = inscriptionService.validerDemandeInscription(idMedecin, approuver, commentaire);
            return ResponseEntity.ok(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la validation de la demande: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("Demande médecin non trouvée: {}", idMedecin);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(creerReponseErreur("Demande non trouvée"));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la validation de la demande", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/demandes/medecins")
    public ResponseEntity<?> listerDemandesMedecins() {
        try {
            List<Medecin> demandes = inscriptionService.listerDemandesValidation();
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des demandes médecins", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/medecin/inscription")
    public ResponseEntity<?> creerMedecin(@Valid @RequestBody CreationMedecinDto dto) {
        try {
            log.info("Création médecin par admin: {}", dto.getEmail());
            AuthResponseDto response = inscriptionService.creerMedecin(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la création du médecin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du médecin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/medecin/nouveau")
    public ResponseEntity<?> creerMedecinNouveau(@Valid @RequestBody NouveauMedecinDto dto) {
        try {
            log.info("Création médecin NOUVEAU par admin: {}", dto.getEmail());
            AuthResponseDto response = inscriptionService.creerMedecinNouveau(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la création du médecin nouveau: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du médecin nouveau", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admin/administrateur/inscription")
    public ResponseEntity<?> creerAdministrateur(@Valid @RequestBody CreationAdminDto dto) {
        try {
            log.info("Création administrateur: {}", dto.getEmail());
            AuthResponseDto response = inscriptionService.creerAdministrateur(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la création administrateur: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création administrateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @GetMapping("/public/verifier-email/{email}")
    public ResponseEntity<?> verifierDisponibiliteEmail(@PathVariable String email) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("disponible", !inscriptionService.emailExiste(email));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PutMapping("/admin/patients/{id}")
    public ResponseEntity<?> modifierPatient(
            @PathVariable Long id,
            @RequestBody ModificationPatientDto dto) {
        try {
            AuthResponseDto response = inscriptionService.modifierPatient(id, dto);
            return ResponseEntity.ok(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la modification patient {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("Patient non trouvé pour id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(creerReponseErreur("Patient non trouvé"));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la modification patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PutMapping("/admin/medecins/{id}")
    public ResponseEntity<?> modifierMedecin(
            @PathVariable Long id,
            @RequestBody ModificationMedecinDto dto) {
        try {
            AuthResponseDto response = inscriptionService.modifierMedecin(id, dto);
            return ResponseEntity.ok(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la modification médecin {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("Médecin non trouvé pour id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(creerReponseErreur("Médecin non trouvé"));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la modification médecin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PutMapping("/admin/administrateurs/{id}")
    public ResponseEntity<?> modifierAdministrateur(
            @PathVariable Long id,
            @RequestBody ModificationAdminDto dto) {
        try {
            AuthResponseDto response = inscriptionService.modifierAdministrateur(id, dto);
            return ResponseEntity.ok(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la modification administrateur {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("Administrateur non trouvé pour id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(creerReponseErreur("Administrateur non trouvé"));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la modification administrateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @DeleteMapping("/admin/patients/{id}")
    public ResponseEntity<?> supprimerPatient(@PathVariable Long id) {
        try {
            inscriptionService.supprimerPatient(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Patient non trouvé pour suppression id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/admin/medecins/{id}")
    public ResponseEntity<?> supprimerMedecin(@PathVariable Long id) {
        try {
            inscriptionService.supprimerMedecin(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Médecin non trouvé pour suppression id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression médecin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/admin/administrateurs/{id}")
    public ResponseEntity<?> supprimerAdministrateur(@PathVariable Long id) {
        try {
            inscriptionService.supprimerAdministrateur(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Administrateur non trouvé pour suppression id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression administrateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/utilisateurs")
    public ResponseEntity<?> listerUtilisateurs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut) {
        try {
            List<UtilisateurDto> utilisateurs = inscriptionService.listerUtilisateurs(type, statut);
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<?> supprimerUtilisateur(@PathVariable Long id) {
        try {
            inscriptionService.supprimerDefinitivement(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Utilisateur non trouvé pour suppression définitive id {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression définitive utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/admin/receptionnistes")
    public ResponseEntity<?> inscrireReceptionniste(@RequestBody @Valid InscriptionReceptionnisteDto dto) {
        try {
            AuthResponseDto response = inscriptionService.inscrireReceptionniste(dto);
            return ResponseEntity.ok(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de l'inscription réceptionniste: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'inscription réceptionniste", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> erreur = new HashMap<>();
        erreur.put("erreur", message);
        return erreur;
    }
}
*/
