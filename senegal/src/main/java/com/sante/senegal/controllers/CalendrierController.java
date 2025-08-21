package com.sante.senegal.controllers;

import com.sante.senegal.entities.JourFerie;
import com.sante.senegal.repositories.JourFerieRepository;
import com.sante.senegal.config.DateNagerCalendrierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendrier")
@Slf4j
public class CalendrierController {

    private final DateNagerCalendrierService calendrierService;

    private final JourFerieRepository jourFerieRepository;

    public CalendrierController(DateNagerCalendrierService calendrierService, JourFerieRepository jourFerieRepository) {
        this.calendrierService = calendrierService;
        this.jourFerieRepository = jourFerieRepository;
    }

    /**
     * Récupère les jours fériés pour une année donnée
     */
    @GetMapping("/jours-feries/{annee}")
    public ResponseEntity<List<JourFerie>> getJoursFeries(@PathVariable int annee) {
        try {
            List<JourFerie> joursFeries = jourFerieRepository.findByAnnee(annee);
            return ResponseEntity.ok(joursFeries);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des jours fériés pour l'année {}: {}",
                    annee, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les jours fériés pour un mois donné
     */
    @GetMapping("/jours-feries/{annee}/{mois}")
    public ResponseEntity<List<JourFerie>> getJoursFeriesDuMois(
            @PathVariable int annee,
            @PathVariable int mois) {
        try {
            List<JourFerie> joursFeries = calendrierService.getJoursFeriesDuMois(mois, annee);
            return ResponseEntity.ok(joursFeries);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des jours fériés pour {}/{}: {}",
                    mois, annee, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifie si une date est un jour férié
     */
    @GetMapping("/est-jour-ferie/{date}")
    public ResponseEntity<Map<String, Object>> estJourFerie(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            boolean estJourFerie = calendrierService.estJourFerie(localDate);

            Map<String, Object> response = new HashMap<>();
            response.put("date", date);
            response.put("estJourFerie", estJourFerie);

            if (estJourFerie) {
                Optional<JourFerie> jourFerie = jourFerieRepository
                        .findByDateAndAffecteDisponibilites(localDate, true);
                jourFerie.ifPresent(jf -> {
                    response.put("nom", jf.getNom());
                    response.put("description", jf.getDescription());
                    response.put("type", jf.getType());
                });
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de la date {}: {}", date, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Synchronisation manuelle des jours fériés
     */
    @PostMapping("/synchroniser/{annee}")
    public ResponseEntity<Map<String, String>> synchroniserJoursFeries(@PathVariable int annee) {
        try {
            calendrierService.synchroniserManuellement(annee);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Synchronisation réussie pour l'année " + annee);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la synchronisation manuelle pour l'année {}: {}",
                    annee, e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la synchronisation");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Test de connectivité avec l'API
     */
    @GetMapping("/test-connectivite")
    public ResponseEntity<Map<String, Object>> testerConnectivite() {
        boolean isConnected = calendrierService.testerConnectivite();

        Map<String, Object> response = new HashMap<>();
        response.put("connected", isConnected);
        response.put("api", "Date Nager");
        response.put("url", "https://date.nager.at/api/v3");
        response.put("timestamp", LocalDateTime.now().toString());

        if (isConnected) {
            response.put("message", "Connectivité OK");
        } else {
            response.put("message", "Connectivité échouée - utilisation des données par défaut");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les pays supportés par Date Nager
     */
    @GetMapping("/pays-supportes")
    public ResponseEntity<List<String>> getPaysSupportes() {
        try {
            List<String> pays = calendrierService.getPaysSupportes();
            return ResponseEntity.ok(pays);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des pays supportés: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les jours fériés entre deux dates
     */
    @GetMapping("/jours-feries")
    public ResponseEntity<List<JourFerie>> getJoursFeriesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<JourFerie> joursFeries = calendrierService.getJoursFeries(debut, fin);
            return ResponseEntity.ok(joursFeries);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des jours fériés entre {} et {}: {}",
                    debut, fin, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Statistiques sur les jours fériés
     */
    @GetMapping("/statistiques/{annee}")
    public ResponseEntity<Map<String, Object>> getStatistiques(@PathVariable int annee) {
        try {
            List<JourFerie> joursFeries = jourFerieRepository.findByAnnee(annee);

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", joursFeries.size());
            stats.put("annee", annee);

            // Grouper par type
            Map<JourFerie.TypeJourFerie, Long> parType = joursFeries.stream()
                    .collect(Collectors.groupingBy(JourFerie::getType, Collectors.counting()));
            stats.put("parType", parType);

            // Grouper par mois
            Map<Integer, Long> parMois = joursFeries.stream()
                    .collect(Collectors.groupingBy(jf -> jf.getDate().getMonthValue(), Collectors.counting()));
            stats.put("parMois", parMois);

            // Source des données
            Map<String, Long> parSource = joursFeries.stream()
                    .collect(Collectors.groupingBy(JourFerie::getSourceApi, Collectors.counting()));
            stats.put("parSource", parSource);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erreur lors de la génération des statistiques pour l'année {}: {}",
                    annee, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}