package com.sante.senegal.controllers;

import com.sante.senegal.dto.PlanningDto;
import com.sante.senegal.dto.PlanningReservationRequestDto;
import com.sante.senegal.services.interfaces.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Planning", description = "API de gestion des plannings médicaux")
public class PlanningController {

    private final PlanningService planningService;

    @PostMapping("/generer")
    @Operation(summary = "Générer les plannings pour un médecin",
            description = "Génère les créneaux de consultation pour un médecin dans un hôpital pour le mois suivant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plannings générés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Médecin ou hôpital non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> genererPlannings(
            @Parameter(description = "ID du médecin", required = true) @RequestParam Long idMedecin,
            @Parameter(description = "ID de l'hôpital", required = true) @RequestParam Long idHopital) {
        try {
            planningService.genererPlanningsPourUnMois(idMedecin, idHopital);
            return ResponseEntity.ok("Plannings générés avec succès pour le médecin " + idMedecin + " à l'hôpital " + idHopital);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la génération des plannings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la génération des plannings: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la génération des plannings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour accepter les formats datetime d'Angular
    @GetMapping("/creneaux-disponibles")
    @Operation(summary = "Récupérer les créneaux disponibles",
            description = "Récupère les créneaux disponibles selon les critères spécifiés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneaux récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<PlanningDto>> getCreneauxDisponibles(
            @Parameter(description = "ID du médecin (optionnel)") @RequestParam(required = false) Long idMedecin,
            @Parameter(description = "ID du service (optionnel)") @RequestParam(required = false) Long idService,
            @Parameter(description = "ID de l'hôpital (optionnel)") @RequestParam(required = false) Long idHopital,
            @Parameter(description = "Date de début", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().build();
            }
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateDebutLocal = dateDebut.toLocalDate();
            LocalDate dateFinLocal = dateFin.toLocalDate();

            List<PlanningDto> creneaux = planningService.getCreneauxDisponiblesDto(idMedecin, idService, idHopital, dateDebutLocal, dateFinLocal);
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour accepter les formats datetime d'Angular
    @GetMapping("/creneaux-disponibles/date")
    @Operation(summary = "Récupérer les créneaux disponibles pour une date",
            description = "Récupère les créneaux disponibles pour une date spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneaux récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Date invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<PlanningDto>> getCreneauxDisponiblesParDate(
            @Parameter(description = "ID du médecin (optionnel)") @RequestParam(required = false) Long idMedecin,
            @Parameter(description = "ID du service (optionnel)") @RequestParam(required = false) Long idService,
            @Parameter(description = "ID de l'hôpital (optionnel)") @RequestParam(required = false) Long idHopital,
            @Parameter(description = "Date", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateLocal = date.toLocalDate();

            List<PlanningDto> creneaux = planningService.getCreneauxDisponiblesParCriteres(idMedecin, idService, idHopital, dateLocal);
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour la cohérence
    @GetMapping("/medecin/{idMedecin}/creneaux-disponibles")
    @Operation(summary = "Récupérer les créneaux disponibles d'un médecin",
            description = "Récupère tous les créneaux disponibles d'un médecin sur une période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneaux récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<PlanningDto>> getCreneauxDisponiblesParMedecin(
            @Parameter(description = "ID du médecin", required = true) @PathVariable Long idMedecin,
            @Parameter(description = "Date de début", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().build();
            }
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateDebutLocal = dateDebut.toLocalDate();
            LocalDate dateFinLocal = dateFin.toLocalDate();

            List<PlanningDto> creneaux = planningService.getCreneauxDisponiblesParMedecin(idMedecin, dateDebutLocal, dateFinLocal);
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux du médecin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour la cohérence
    @GetMapping("/service/{idService}/creneaux-disponibles")
    @Operation(summary = "Récupérer les créneaux disponibles d'un service",
            description = "Récupère tous les créneaux disponibles d'un service sur une période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneaux récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Service non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<PlanningDto>> getCreneauxDisponiblesParService(
            @Parameter(description = "ID du service", required = true) @PathVariable Long idService,
            @Parameter(description = "Date de début", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().build();
            }
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateDebutLocal = dateDebut.toLocalDate();
            LocalDate dateFinLocal = dateFin.toLocalDate();

            List<PlanningDto> creneaux = planningService.getCreneauxDisponiblesParService(idService, dateDebutLocal, dateFinLocal);
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux du service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour la cohérence
    @GetMapping("/hopital/{idHopital}/creneaux-disponibles")
    @Operation(summary = "Récupérer les créneaux disponibles d'un hôpital",
            description = "Récupère tous les créneaux disponibles d'un hôpital sur une période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneaux récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Hôpital non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<PlanningDto>> getCreneauxDisponiblesParHopital(
            @Parameter(description = "ID de l'hôpital", required = true) @PathVariable Long idHopital,
            @Parameter(description = "Date de début", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            if (dateDebut.isAfter(dateFin)) {
                return ResponseEntity.badRequest().build();
            }
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateDebutLocal = dateDebut.toLocalDate();
            LocalDate dateFinLocal = dateFin.toLocalDate();

            List<PlanningDto> creneaux = planningService.getCreneauxDisponiblesParHopital(idHopital, dateDebutLocal, dateFinLocal);
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux de l'hôpital: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reserver")
    @Operation(summary = "Réserver un créneau",
            description = "Réserve un créneau de consultation pour un patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau réservé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de réservation invalides"),
            @ApiResponse(responseCode = "404", description = "Planning ou patient non trouvé"),
            @ApiResponse(responseCode = "409", description = "Créneau déjà réservé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> reserverCreneau(
            @Parameter(description = "Données de réservation", required = true)
            @Valid @RequestBody PlanningReservationRequestDto request) {
        try {
            PlanningDto planningReserve = planningService.reserverCreneau(request);
            return ResponseEntity.ok(planningReserve);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la réservation: {}", e.getMessage());
            if (e.getMessage().contains("déjà réservé")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Le créneau est déjà réservé");
            } else if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la réservation: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la réservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

    @PutMapping("/liberer/{idPlanning}")
    @Operation(summary = "Libérer un créneau",
            description = "Libère un créneau réservé (annule le rendez-vous)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Créneau libéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Planning non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> libererCreneau(
            @Parameter(description = "ID du planning à libérer", required = true)
            @PathVariable Long idPlanning) {
        try {
            PlanningDto planningLibere = planningService.libererCreneau(idPlanning);
            return ResponseEntity.ok(planningLibere);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la libération du créneau: {}", e.getMessage());
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la libération: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la libération: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

    @GetMapping("/disponible/{idPlanning}")
    @Operation(summary = "Vérifier la disponibilité d'un créneau",
            description = "Vérifie si un créneau est disponible pour réservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut de disponibilité retourné"),
            @ApiResponse(responseCode = "404", description = "Planning non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Boolean> isCreneauDisponible(
            @Parameter(description = "ID du planning", required = true) @PathVariable Long idPlanning) {
        try {
            boolean disponible = planningService.isCreneauDisponible(idPlanning);
            return ResponseEntity.ok(disponible);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de disponibilité: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MODIFIÉ: Changé LocalDate vers LocalDateTime pour la cohérence
    @GetMapping("/count/disponibles")
    @Operation(summary = "Compter les créneaux disponibles",
            description = "Compte le nombre de créneaux disponibles pour un médecin à une date donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre de créneaux disponibles retourné"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Long> countCreneauxDisponibles(
            @Parameter(description = "ID du médecin", required = true) @RequestParam Long idMedecin,
            @Parameter(description = "Date", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            // Convertir LocalDateTime vers LocalDate pour l'appel au service
            LocalDate dateLocal = date.toLocalDate();

            long count = planningService.countCreneauxDisponibles(idMedecin, dateLocal);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des créneaux disponibles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}