package com.sante.senegal.controllers;

import com.sante.senegal.dto.HopitalDto;
import com.sante.senegal.dto.HopitalRequestDto;
import com.sante.senegal.entities.Hopital.StatutHopital;
import com.sante.senegal.services.interfaces.HopitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hopitaux")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HopitalController {

    private final HopitalService hopitalService;

    @GetMapping
    public ResponseEntity<List<HopitalDto>> getAllHopitaux() {
        return ResponseEntity.ok(hopitalService.getAllHopitaux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HopitalDto> getHopitalById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hopitalService.getHopitalById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<HopitalDto>> getHopitauxByStatut(@PathVariable StatutHopital statut) {
        return ResponseEntity.ok(hopitalService.getHopitauxByStatut(statut));
    }

    @GetMapping("/ville/{ville}")
    public ResponseEntity<List<HopitalDto>> getHopitauxByVille(@PathVariable String ville) {
        return ResponseEntity.ok(hopitalService.getHopitauxByVille(ville));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<HopitalDto>> getHopitauxByRegion(@PathVariable String region) {
        return ResponseEntity.ok(hopitalService.getHopitauxByRegion(region));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HopitalDto>> searchHopitauxByNom(@RequestParam String nom) {
        return ResponseEntity.ok(hopitalService.searchHopitauxByNom(nom));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<HopitalDto>> getHopitauxByType(@PathVariable String type) {
        return ResponseEntity.ok(hopitalService.getHopitauxByTypeEtablissement(type));
    }

    @GetMapping("/capacite/{capaciteMin}")
    public ResponseEntity<List<HopitalDto>> getHopitauxByCapacite(@PathVariable Integer capaciteMin) {
        return ResponseEntity.ok(hopitalService.getHopitauxByCapaciteMinimum(capaciteMin));
    }

    @PostMapping
    public ResponseEntity<HopitalDto> createHopital(@Valid @RequestBody HopitalRequestDto dto) {
        try {
            HopitalDto hopital = hopitalService.createHopital(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(hopital);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HopitalDto> updateHopital(@PathVariable Long id, @Valid @RequestBody HopitalRequestDto dto) {
        try {
            HopitalDto hopitalMisAJour = hopitalService.updateHopital(id, dto);
            return ResponseEntity.ok(hopitalMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHopital(@PathVariable Long id) {
        try {
            hopitalService.deleteHopital(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<HopitalDto> changeStatutHopital(@PathVariable Long id, @RequestBody StatutHopital statut) {
        try {
            HopitalDto hopitalMisAJour = hopitalService.changeStatutHopital(id, statut);
            return ResponseEntity.ok(hopitalMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
