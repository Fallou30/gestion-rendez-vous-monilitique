package com.sante.senegal.controllers;

import com.sante.senegal.dto.RendezVousDto;
import com.sante.senegal.dto.RendezVousRequestDto;
import com.sante.senegal.entities.RendezVous;
import com.sante.senegal.services.interfaces.RendezVousService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rendez-vous")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @GetMapping
    public ResponseEntity<List<RendezVousDto>> getAll() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }
    @GetMapping("/medecins/{idMedecin}/programmes")
    public ResponseEntity<List<RendezVousDto>> getRendezVousProgrammesPourMedecin(@PathVariable Long idMedecin) {
        List<RendezVousDto> rdvs = rendezVousService.getRendezVousProgrammesPourMedecin(idMedecin);
        return ResponseEntity.ok(rdvs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }

    @PostMapping
    public ResponseEntity<RendezVousDto> create(@Valid @RequestBody RendezVousRequestDto dto) {
        return ResponseEntity
                .status(201)
                .body(rendezVousService.createRendezVous(dto));
    }

    @PatchMapping("/{id}/date")
    public ResponseEntity<RendezVousDto> updateDate(
            @PathVariable Long id,
            @RequestBody RendezVousRequestDto dto) {
        var updated = rendezVousService.updateDateHeureRendezVous(
                id, dto.getDateHeure(), dto.getDureePrevue()
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendezVousDto> update(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousRequestDto dto) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RendezVousDto> changeStatut(
            @PathVariable Long id,
            @RequestBody String nouveauStatut) {
        return ResponseEntity.ok(
                rendezVousService.changeStatutRendezVous(id,
                        Enum.valueOf(com.sante.senegal.entities.RendezVous.StatutRendezVous.class, nouveauStatut)
                )
        );
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<RendezVousDto> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.confirmerRendezVous(id));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<RendezVousDto> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.annulerRendezVous(id));
    }

    @PatchMapping("/{id}/reporter")
    public ResponseEntity<RendezVousDto> reporter(
            @PathVariable Long id,
            @RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nouvelleDate) {
        return ResponseEntity.ok(rendezVousService.reporterRendezVous(id, nouvelleDate));
    }

    @PatchMapping("/{id}/commencer")
    public ResponseEntity<RendezVousDto> commencer(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.commencerConsultation(id));
    }

    @PatchMapping("/{id}/terminer")
    public ResponseEntity<RendezVousDto> terminer(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.terminerConsultation(id));
    }

    @GetMapping("/patient/{idPatient}")
    public ResponseEntity<List<RendezVousDto>> getByPatient(@PathVariable Long idPatient) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatient(idPatient));
    }

    @GetMapping("/medecin/{idMedecin}")
    public ResponseEntity<List<RendezVousDto>> getByMedecin(@PathVariable Long idMedecin) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecin(idMedecin));
    }

    @GetMapping("/service/{idService}")
    public ResponseEntity<List<RendezVousDto>> getByService(@PathVariable Long idService) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByService(idService));
    }

    @GetMapping("/hopital/{idHopital}")
    public ResponseEntity<List<RendezVousDto>> getByHopital(@PathVariable Long idHopital) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByHopital(idHopital));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<RendezVousDto>> getByStatut(@PathVariable RendezVous.StatutRendezVous statut) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByStatut(statut));
    }

    @GetMapping("/urgence/{niveauUrgence}")
    public ResponseEntity<List<RendezVousDto>> getByUrgence(@PathVariable RendezVous.NiveauUrgence niveauUrgence) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByNiveauUrgence(niveauUrgence));
    }

    @GetMapping("/periode")
    public ResponseEntity<List<RendezVousDto>> getBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(rendezVousService.getRendezVousBetweenDates(dateDebut, dateFin));
    }

    @GetMapping("/medecin/{idMedecin}/periode")
    public ResponseEntity<List<RendezVousDto>> getByMedecinAndPeriode(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecinAndDateRange(idMedecin, dateDebut, dateFin));
    }

    @GetMapping("/patient/{idPatient}/prochains")
    public ResponseEntity<List<RendezVousDto>> getProchainsByPatient(@PathVariable Long idPatient) {
        return ResponseEntity.ok(rendezVousService.getUpcomingRendezVousByPatient(idPatient));
    }

    @GetMapping("/urgents")
    public ResponseEntity<List<RendezVousDto>> getUrgents() {
        return ResponseEntity.ok(rendezVousService.getRendezVousUrgents());
    }

    @GetMapping("/aujourd-hui")
    public ResponseEntity<List<RendezVousDto>> getToday() {
        return ResponseEntity.ok(rendezVousService.getRendezVousDuJour(LocalDateTime.now()));
    }

    @GetMapping("/en-retard")
    public ResponseEntity<List<RendezVousDto>> getOverdue() {
        return ResponseEntity.ok(rendezVousService.getOverdueRendezVous());
    }
    @GetMapping("/medecin/{idMedecin}/prochains")
    public ResponseEntity<List<RendezVousDto>> getProchainsByMedecin(@PathVariable Long idMedecin) {
        return ResponseEntity.ok(rendezVousService.getUpcomingRendezVousByMedecin(idMedecin));
    }


    @GetMapping("/medecin/{idMedecin}/count")
    public ResponseEntity<Long> countByMedecinAndDate(
            @PathVariable Long idMedecin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        return ResponseEntity.ok(rendezVousService.countRendezVousByMedecinAndDate(idMedecin, dateDebut, dateFin));
    }

}
