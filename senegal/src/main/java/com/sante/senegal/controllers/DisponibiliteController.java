package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Disponibilite;
import com.sante.senegal.services.interfaces.DisponibiliteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/disponibilites")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    // ===== CRUD de base =====

    @GetMapping
    public ResponseEntity<List<DisponibiliteResponseDto>> getAllDisponibilites() {
        log.info("Récupération de toutes les disponibilités");
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getAllDisponibilites();
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibiliteResponseDto> getDisponibiliteById(@PathVariable Long id) {
        log.info("Récupération de la disponibilité avec ID: {}", id);
        Optional<DisponibiliteResponseDto> disponibilite = disponibiliteService.getDisponibiliteById(id);
        return disponibilite
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DisponibiliteResponseDto> createDisponibilite(@Valid @RequestBody DisponibiliteRequestDto requestDto) {
        log.info("Création d'une nouvelle disponibilité pour le médecin: {}", requestDto.getIdMedecin());
        try {
            DisponibiliteResponseDto disponibilite = disponibiliteService.createDisponibilite(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(disponibilite);
        } catch (Exception e) {
            log.error("Erreur lors de la création de la disponibilité: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibiliteResponseDto> updateDisponibilite(
            @PathVariable Long id,
            @Valid @RequestBody DisponibiliteRequestDto requestDto) {
        log.info("Mise à jour de la disponibilité avec ID: {}", id);
        try {
            DisponibiliteResponseDto disponibilite = disponibiliteService.updateDisponibilite(id, requestDto);
            return ResponseEntity.ok(disponibilite);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la disponibilité: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        log.info("Suppression de la disponibilité avec ID: {}", id);
        try {
            disponibiliteService.deleteDisponibilite(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la disponibilité: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== Recherches simples =====

    @GetMapping("/medecin/{idMedecin}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByMedecin(@PathVariable Long idMedecin) {
        log.info("Récupération des disponibilités pour le médecin: {}", idMedecin);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByMedecin(idMedecin);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/service/{idService}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByService(@PathVariable Long idService) {
        log.info("Récupération des disponibilités pour le service: {}", idService);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByService(idService);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/hopital/{idHopital}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByHopital(@PathVariable Long idHopital) {
        log.info("Récupération des disponibilités pour l'hôpital: {}", idHopital);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByHopital(idHopital);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Récupération des disponibilités pour la date: {}", date);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByDate(date);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByStatut(
            @PathVariable Disponibilite.StatutDisponibilite statut) {
        log.info("Récupération des disponibilités avec le statut: {}", statut);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByStatut(statut);
        return ResponseEntity.ok(disponibilites);
    }

    // ===== Recherches avancées =====

    @GetMapping("/recherche")
    public ResponseEntity<List<DisponibiliteResponseDto>> rechercherDisponibilites(
            @RequestParam(required = false) Long idMedecin,
            @RequestParam(required = false) Long idService,
            @RequestParam(required = false) Long idHopital,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Disponibilite.StatutDisponibilite statut) {

        log.info("Recherche de disponibilités avec critères multiples");
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.rechercherDisponibilites(
                idMedecin, idService, idHopital, dateDebut, dateFin, statut);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/periode")
    public ResponseEntity<List<DisponibiliteResponseDto>> getAvailableDisponibilitesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Récupération des disponibilités disponibles entre {} et {}", dateDebut, dateFin);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getAvailableDisponibilitesBetweenDates(dateDebut, dateFin);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/service/{idService}/periode")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByServiceBetweenDates(
            @PathVariable Long idService,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Récupération des disponibilités du service {} entre {} et {}", idService, dateDebut, dateFin);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByServiceBetweenDates(idService, dateDebut, dateFin);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/hopital/{idHopital}/periode")
    public ResponseEntity<List<DisponibiliteResponseDto>> getDisponibilitesByHopitalBetweenDates(
            @PathVariable Long idHopital,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Récupération des disponibilités de l'hôpital {} entre {} et {}", idHopital, dateDebut, dateFin);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getDisponibilitesByHopitalBetweenDates(idHopital, dateDebut, dateFin);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/medecin/{idMedecin}/date/{date}/statut/{statut}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getByMedecinDateAndStatut(
            @PathVariable Long idMedecin,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Disponibilite.StatutDisponibilite statut) {
        log.info("Récupération des disponibilités du médecin {} pour la date {} avec le statut {}", idMedecin, date, statut);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getByMedecinDateAndStatut(idMedecin, date, statut);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/service/{idService}/date/{date}/statut/{statut}")
    public ResponseEntity<List<DisponibiliteResponseDto>> getByServiceDateAndStatut(
            @PathVariable Long idService,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Disponibilite.StatutDisponibilite statut) {
        log.info("Récupération des disponibilités du service {} pour la date {} avec le statut {}", idService, date, statut);
        List<DisponibiliteResponseDto> disponibilites = disponibiliteService.getByServiceDateAndStatut(idService, date, statut);
        return ResponseEntity.ok(disponibilites);
    }

    // ===== Vérifications et validations =====

    @GetMapping("/conflit")
    public ResponseEntity<Boolean> isConflictingDisponibilite(
            @RequestParam Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin) {
        log.info("Vérification de conflit pour le médecin {} le {} de {} à {}", idMedecin, date, heureDebut, heureFin);
        boolean hasConflict = disponibiliteService.isConflictingDisponibilite(idMedecin, date, heureDebut, heureFin);
        return ResponseEntity.ok(hasConflict);
    }

    @GetMapping("/medecin/{idMedecin}/disponible")
    public ResponseEntity<Boolean> estDisponible(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure) {
        log.info("Vérification de disponibilité du médecin {} à {}", idMedecin, dateHeure);
        boolean estDisponible = disponibiliteService.estDisponible(idMedecin, dateHeure);
        return ResponseEntity.ok(estDisponible);
    }

    @GetMapping("/medecin/{idMedecin}/disponible-periode")
    public ResponseEntity<Boolean> estMedecinDisponible(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin) {
        log.info("Vérification de disponibilité du médecin {} le {} de {} à {}", idMedecin, date, heureDebut, heureFin);
        boolean estDisponible = disponibiliteService.estMedecinDisponible(idMedecin, date, heureDebut, heureFin);
        return ResponseEntity.ok(estDisponible);
    }

    // ===== Gestion des créneaux =====

    @GetMapping("/medecin/{idMedecin}/creneaux-libres/{date}")
    public ResponseEntity<List<CreneauLibre>> getCreneauxLibres(
            @PathVariable Long idMedecin,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Récupération des créneaux libres pour le médecin {} le {}", idMedecin, date);
        List<CreneauLibre> creneaux = disponibiliteService.getCreneauxLibres(idMedecin, date);
        return ResponseEntity.ok(creneaux);
    }

    // ===== Gestion des statuts =====

    @PutMapping("/{id}/statut")
    public ResponseEntity<DisponibiliteResponseDto> changeStatutDisponibilite(
            @PathVariable Long id,
            @RequestParam Disponibilite.StatutDisponibilite nouveauStatut) {
        log.info("Changement de statut de la disponibilité {} vers {}", id, nouveauStatut);
        try {
            DisponibiliteResponseDto disponibilite = disponibiliteService.changeStatutDisponibilite(id, nouveauStatut);
            return ResponseEntity.ok(disponibilite);
        } catch (Exception e) {
            log.error("Erreur lors du changement de statut: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== Gestion des absences =====

    @PostMapping("/medecin/{idMedecin}/absence")
    public ResponseEntity<String> marquerAbsence(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam String motif) {
        log.info("Marquage d'absence pour le médecin {} du {} au {} - motif: {}", idMedecin, dateDebut, dateFin, motif);
        try {
            disponibiliteService.marquerAbsence(idMedecin, dateDebut, dateFin, motif);
            return ResponseEntity.ok("Absence marquée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du marquage d'absence: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // ===== Génération de disponibilités =====

    @PostMapping("/generer")
    public ResponseEntity<String> genererDisponibilites(
            @RequestParam Long idMedecin,
            @RequestParam Long idService,
            @RequestParam Long idHopital,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestBody List<CreneauHoraire> creneaux) {
        log.info("Génération de disponibilités pour le médecin {} du {} au {}", idMedecin, dateDebut, dateFin);
        try {
            disponibiliteService.genererDisponibilites(idMedecin, idService, idHopital, dateDebut, dateFin, creneaux);
            return ResponseEntity.ok("Disponibilités générées avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la génération des disponibilités: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // ===== Planning et statistiques =====

    @GetMapping("/medecin/{idMedecin}/planning")
    public ResponseEntity<Map<LocalDate, List<DisponibiliteResponseDto>>> getPlanningMedecin(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Récupération du planning du médecin {} du {} au {}", idMedecin, dateDebut, dateFin);
        Map<LocalDate, List<DisponibiliteResponseDto>> planning = disponibiliteService.getPlanningMedecin(idMedecin, dateDebut, dateFin);
        return ResponseEntity.ok(planning);
    }

    @GetMapping("/medecin/{idMedecin}/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesDisponibilites(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Récupération des statistiques de disponibilité du médecin {} du {} au {}", idMedecin, dateDebut, dateFin);
        Map<String, Object> statistiques = disponibiliteService.getStatistiquesDisponibilites(idMedecin, dateDebut, dateFin);
        return ResponseEntity.ok(statistiques);
    }

    // ===== Vérifications de cohérence =====

    @GetMapping("/medecin/{idMedecin}/coherence")
    public ResponseEntity<List<String>> verifierCoherenceDisponibilites(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        log.info("Vérification de cohérence des disponibilités du médecin {} du {} au {}", idMedecin, dateDebut, dateFin);
        List<String> problemes = disponibiliteService.verifierCoherenceDisponibilites(idMedecin, dateDebut, dateFin);
        return ResponseEntity.ok(problemes);
    }

    // ===== Gestion des erreurs globales =====

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Argument invalide: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Argument invalide: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Erreur runtime: {}", e.getMessage());
        return ResponseEntity.internalServerError().body("Erreur interne: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erreur inattendue: {}", e.getMessage());
        return ResponseEntity.internalServerError().body("Erreur inattendue: " + e.getMessage());
    }
}