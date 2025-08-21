package com.sante.senegal.controllers;

import com.sante.senegal.dto.DashboardMedecinStatDto;
import com.sante.senegal.dto.MedicamentPrescritStat;
import com.sante.senegal.services.implementations.StatistiquesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistiques")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    /**
     * Statistiques des consultations par médecin
     */
    @GetMapping("/consultations/medecin/{medecinId}")
    public ResponseEntity<Map<String, Object>> getStatistiquesConsultationsMedecin(@PathVariable Long medecinId,
                                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        Map<String, Object> stats = statistiquesService.getStatistiquesConsultationsMedecin(medecinId, dateDebut, dateFin);
        return ResponseEntity.ok(stats);
    }

    /**
     * Statistiques des prescriptions
     */
    @GetMapping("/prescriptions")
    public ResponseEntity<Map<String, Object>> getStatistiquesPrescriptions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        Map<String, Object> stats = statistiquesService.getStatistiquesPrescriptions(dateDebut, dateFin);
        return ResponseEntity.ok(stats);
    }

    /**
     * Top des médicaments les plus prescrits
     */
    @GetMapping("/medicaments/top")
    public ResponseEntity<List<MedicamentPrescritStat>> getTopMedicamentsPrescrits(@RequestParam(defaultValue = "10") int limit) {
        List<MedicamentPrescritStat> stats = statistiquesService.getTopMedicamentsPrescrits(limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Pour avoir les infos Tableau de board du medecin
     */
    @GetMapping("/dashboard/medecin/{id}")
    public ResponseEntity<DashboardMedecinStatDto> getDashboardStatsMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(statistiquesService.getDashboardStatsMedecin(id));
    }

}