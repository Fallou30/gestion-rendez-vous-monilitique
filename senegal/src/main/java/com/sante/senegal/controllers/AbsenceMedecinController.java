package com.sante.senegal.controllers;

import com.sante.senegal.entities.AbsenceMedecin;
import com.sante.senegal.services.interfaces.AbsenceMedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/absences-medecins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // à adapter pour la sécurité en prod
public class AbsenceMedecinController {

    private final AbsenceMedecinService absenceMedecinService;

    @PostMapping
    public ResponseEntity<AbsenceMedecin> creerAbsence(@RequestBody AbsenceMedecin absenceMedecin) {
        AbsenceMedecin abs = absenceMedecinService.creerAbsence(absenceMedecin);
        return ResponseEntity.ok(abs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbsenceMedecin> modifierAbsence(@PathVariable Long id, @RequestBody AbsenceMedecin absence) {
        AbsenceMedecin abs = absenceMedecinService.modifierAbsence(id, absence);
        return ResponseEntity.ok(abs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAbsence(@PathVariable Long id) {
        absenceMedecinService.supprimerAbsence(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbsenceMedecin> getAbsenceById(@PathVariable Long id) {
        return absenceMedecinService.getAbsenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<AbsenceMedecin>> getAbsencesByMedecin(@PathVariable Long medecinId) {
        List<AbsenceMedecin> absences = absenceMedecinService.getAbsencesByMedecin(medecinId);
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/medecin/{medecinId}/estAbsent")
    public ResponseEntity<Boolean> estAbsent(
            @PathVariable Long medecinId,
            @RequestParam("date") String dateStr) {

        LocalDate date = LocalDate.parse(dateStr);
        boolean absent = absenceMedecinService.estAbsent(medecinId, date);

        return ResponseEntity.ok(absent);
    }
}
