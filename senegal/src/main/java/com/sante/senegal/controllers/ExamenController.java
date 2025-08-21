package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.services.interfaces.ExamenService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/examens")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExamenController {

    private final ExamenService examenService;

    @PostMapping
    public ResponseEntity<ExamenDto> prescrireExamen(@RequestBody CreateExamenRequest createExamenDto) {
        try {
            return ResponseEntity.ok(examenService.prescrireExamen(createExamenDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/programmer")
    public ResponseEntity<ExamenDto> programmerExamen(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateRealisation) {
        try {
            return ResponseEntity.ok(examenService.programmerExamen(id, dateRealisation));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/resultats")
    public ResponseEntity<ExamenDto> saisirResultats(
            @PathVariable Long id,
            @RequestBody ExamenResultatsRequest resultatExamenDto) {
        try {
            return ResponseEntity.ok(examenService.saisirResultats(id, resultatExamenDto));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ExamenDto>> getExamensPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(examenService.getExamensPatient(patientId));
    }

    @GetMapping("/en-attente")
    public ResponseEntity<List<ExamenDto>> getExamensEnAttente() {
        return ResponseEntity.ok(examenService.getExamensEnAttente());
    }

    @GetMapping("/urgents")
    public ResponseEntity<List<ExamenDto>> getExamensUrgents() {
        return ResponseEntity.ok(examenService.getExamensUrgents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamenDto> getExamen(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(examenService.getExamenById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}