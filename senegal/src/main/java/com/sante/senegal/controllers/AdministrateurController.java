package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.services.interfaces.AdministrateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "Endpoints pour la gestion administrative")
@PreAuthorize("hasRole('ADMIN')")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    // ===============================
    // GESTION DES MÉDECINS
    // ===============================

    @PostMapping("/medecins")
    @Operation(summary = "Créer un médecin", description = "Crée un nouveau médecin avec toutes ses informations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médecin créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<AuthResponseDto> creerMedecin(@Valid @RequestBody CreationMedecinDto dto) {
        log.info("Demande de création de médecin par admin pour email: {}", dto.getEmail());
        AuthResponseDto response = administrateurService.creerMedecin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/medecins/nouveau")
    @Operation(summary = "Créer un nouveau médecin", description = "Crée un médecin avec informations minimales en attente de validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médecin créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<AuthResponseDto> creerMedecinNouveau(@Valid @RequestBody NouveauMedecinDto dto) {
        log.info("Demande de création de nouveau médecin pour email: {}", dto.getEmail());
        AuthResponseDto response = administrateurService.creerMedecinNouveau(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/medecins/{id}")
    @Operation(summary = "Modifier un médecin", description = "Met à jour les informations d'un médecin existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecin modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponseDto> modifierMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Valid @RequestBody ModificationMedecinDto dto) {
        log.info("Demande de modification du médecin ID: {}", id);
        AuthResponseDto response = administrateurService.modifierMedecin(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/medecins/{id}")
    @Operation(summary = "Supprimer un médecin", description = "Désactive un médecin (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Médecin supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    })
    public ResponseEntity<Void> supprimerMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        log.info("Demande de suppression du médecin ID: {}", id);
        administrateurService.supprimerMedecin(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // VALIDATION DES DEMANDES
    // ===============================

    @GetMapping("/medecins/demandes-validation")
    @Operation(summary = "Lister les demandes de validation", description = "Récupère la liste des médecins en attente de validation")
    @ApiResponse(responseCode = "200", description = "Liste des demandes récupérée avec succès")
    public ResponseEntity<List<Medecin>> listerDemandesValidation() {
        log.info("Demande de liste des demandes de validation");
        List<Medecin> demandes = administrateurService.listerDemandesValidation();
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/medecins/{id}/validation")
    @Operation(summary = "Valider une demande d'inscription", description = "Approuve ou rejette une demande d'inscription de médecin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande traitée avec succès"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé"),
            @ApiResponse(responseCode = "400", description = "Demande déjà traitée")
    })
    public ResponseEntity<AuthResponseDto> validerDemandeInscription(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        boolean approuver = (Boolean) payload.get("approuver");
        String commentaire = (String) payload.getOrDefault("commentaire", "");

        log.info("Demande de validation pour médecin ID: {}, approuver: {}", id, approuver);
        AuthResponseDto response = administrateurService.validerDemandeInscription(id, approuver, commentaire);
        return ResponseEntity.ok(response);
    }

    // ===============================
    // GESTION DES PATIENTS
    // ===============================

    @PutMapping("/patients/{id}")
    @Operation(summary = "Modifier un patient", description = "Met à jour les informations d'un patient existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponseDto> modifierPatient(
            @Parameter(description = "ID du patient") @PathVariable Long id,
            @Valid @RequestBody ModificationPatientDto dto) {
        log.info("Demande de modification du patient ID: {}", id);
        AuthResponseDto response = administrateurService.modifierPatient(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/patients/{id}")
    @Operation(summary = "Supprimer un patient", description = "Désactive un patient (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    })
    public ResponseEntity<Void> supprimerPatient(
            @Parameter(description = "ID du patient") @PathVariable Long id) {
        log.info("Demande de suppression du patient ID: {}", id);
        administrateurService.supprimerPatient(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // GESTION DES RÉCEPTIONNISTES
    // ===============================

    @PostMapping("/receptionistes")
    @Operation(summary = "Inscrire un réceptionniste", description = "Crée un nouveau compte réceptionniste")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réceptionniste créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<AuthResponseDto> inscrireReceptionniste(@Valid @RequestBody InscriptionReceptionnisteDto dto) {
        log.info("Demande d'inscription de réceptionniste pour email: {}", dto.getEmail());
        AuthResponseDto response = administrateurService.inscrireReceptionniste(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/receptionistes/{id}")
    @Operation(summary = "Modifier un réceptionniste", description = "Met à jour les informations d'un réceptionniste existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réceptionniste modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Réceptionniste non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponseDto> modifierReceptionniste(
            @Parameter(description = "ID du réceptionniste") @PathVariable Long id,
            @Valid @RequestBody ModificationReceptionnisteDto dto) {
        log.info("Demande de modification du réceptionniste ID: {}", id);
        AuthResponseDto response = administrateurService.modifierReceptionniste(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/receptionistes/{id}")
    @Operation(summary = "Supprimer un réceptionniste", description = "Désactive un réceptionniste (suppression logique)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réceptionniste supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Réceptionniste non trouvé")
    })
    public ResponseEntity<Void> supprimerReceptionniste(
            @Parameter(description = "ID du réceptionniste") @PathVariable Long id) {
        log.info("Demande de suppression du réceptionniste ID: {}", id);
        administrateurService.supprimerReceptionniste(id);
        return ResponseEntity.noContent().build();
    }
    // ===============================
// GESTION DES ADMINISTRATEURS
// ===============================

    @PutMapping("/admins/{id}")
    @Operation(summary = "Modifier un administrateur", description = "Met à jour les informations d'un administrateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrateur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Administrateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponseDto> modifierAdministrateur(
            @Parameter(description = "ID de l'administrateur") @PathVariable Long id,
            @Valid @RequestBody ModificationAdminDto dto) {

        log.info("Demande de modification de l'administrateur ID: {}", id);
        AuthResponseDto response = administrateurService.modifierAdministrateur(id, dto);
        return ResponseEntity.ok(response);
    }


    // ===============================
    // GESTION GÉNÉRALE DES UTILISATEURS
    // ===============================

    @GetMapping("/utilisateurs")
    @Operation(summary = "Lister les utilisateurs", description = "Récupère la liste des utilisateurs avec filtres optionnels")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    public ResponseEntity<List<UtilisateurDto>> listerUtilisateurs(
            @Parameter(description = "Type d'utilisateur à filtrer") @RequestParam(required = false) Utilisateur.TypeUtilisateur type,
            @Parameter(description = "Statut d'utilisateur à filtrer") @RequestParam(required = false)Utilisateur.StatutUtilisateur statut) {
        log.info("Demande de liste des utilisateurs - type: {}, statut: {}", type , statut);
        List<UtilisateurDto> utilisateurs = administrateurService.listerUtilisateurs(type, statut);
        return ResponseEntity.ok(utilisateurs);
    }
    @PutMapping("/utilisateurs/{id}/statut")
    public ResponseEntity<UtilisateurDto> changerStatut(
            @PathVariable Long id,
            @RequestBody ChangerStatutDto dto
    ) {
        UtilisateurDto utilisateurMisAJour = administrateurService.changerStatutUtilisateur(id, dto.getStatut());
        return ResponseEntity.ok(utilisateurMisAJour);
    }


    @DeleteMapping("/utilisateurs/{id}/definitif")
    @Operation(summary = "Supprimer définitivement un utilisateur", description = "Supprime définitivement un utilisateur de la base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé définitivement"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> supprimerDefinitivement(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        log.warn("Demande de suppression définitive de l'utilisateur ID: {}", id);
        administrateurService.supprimerDefinitivement(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // ENDPOINTS DE STATISTIQUES
    // ===============================

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir les statistiques", description = "Récupère les statistiques générales du système")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    public ResponseEntity<Map<String, Object>> obtenirStatistiques() {
        log.info("Demande de statistiques générales");

        List<UtilisateurDto> tousUtilisateurs = administrateurService.listerUtilisateurs(Utilisateur.TypeUtilisateur.ADMIN, Utilisateur.StatutUtilisateur.ACTIF);
        List<UtilisateurDto> medecins = administrateurService.listerUtilisateurs(Utilisateur.TypeUtilisateur.MEDECIN, Utilisateur.StatutUtilisateur.ACTIF);
        List<UtilisateurDto> patients = administrateurService.listerUtilisateurs(Utilisateur.TypeUtilisateur.PATIENT, Utilisateur.StatutUtilisateur.ACTIF);
        List<UtilisateurDto> receptionistes = administrateurService.listerUtilisateurs(Utilisateur.TypeUtilisateur.RECEPTIONNISTE,  Utilisateur.StatutUtilisateur.ACTIF);
        List<Medecin> demandesEnAttente = administrateurService.listerDemandesValidation();

        Map<String, Object> statistiques = Map.of(
                "totalUtilisateurs", tousUtilisateurs.size(),
                "totalMedecins", medecins.size(),
                "totalPatients", patients.size(),
                "totalReceptionistes", receptionistes.size(),
                "demandesEnAttente", demandesEnAttente.size()
        );

        return ResponseEntity.ok(statistiques);
    }

    // ===============================
    // GESTION DES ERREURS
    // ===============================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Erreur dans AdministrateurController: ", e);
        Map<String, String> error = Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}