package com.sante.senegal.controllers;

import com.sante.senegal.dto.ServiceDto;
import com.sante.senegal.dto.ServiceRequestDto;
import com.sante.senegal.entities.Service.StatutService;
import com.sante.senegal.services.interfaces.ServiceHospitalierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceHospitalierService serviceHospitalierService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceHospitalierService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serviceHospitalierService.getServiceById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hopital/{idHopital}")
    public ResponseEntity<List<ServiceDto>> getServicesByHopital(@PathVariable Long idHopital) {
        return ResponseEntity.ok(serviceHospitalierService.getServicesByHopital(idHopital));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ServiceDto>> getServicesByStatut(@PathVariable StatutService statut) {
        return ResponseEntity.ok(serviceHospitalierService.getServicesByStatut(statut));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceDto>> searchServicesByNom(@RequestParam String nom) {
        return ResponseEntity.ok(serviceHospitalierService.searchServicesByNom(nom));
    }

    @GetMapping("/chef/{idChef}")
    public ResponseEntity<List<ServiceDto>> getServicesByChef(@PathVariable Long idChef) {
        return ResponseEntity.ok(serviceHospitalierService.getServicesByChefService(idChef));
    }

    @GetMapping("/ville/{ville}/actifs")
    public ResponseEntity<List<ServiceDto>> getActiveServicesByVille(@PathVariable String ville) {
        return ResponseEntity.ok(serviceHospitalierService.getActiveServicesByVille(ville));
    }

    @PostMapping
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceRequestDto dto) {
        try {
            ServiceDto nouveauService = serviceHospitalierService.createService(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequestDto dto) {
        try {
            ServiceDto serviceMisAJour = serviceHospitalierService.updateService(id, dto);
            return ResponseEntity.ok(serviceMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            serviceHospitalierService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<ServiceDto> changeStatutService(@PathVariable Long id, @RequestBody StatutService statut) {
        try {
            ServiceDto serviceMisAJour = serviceHospitalierService.changeStatutService(id, statut);
            return ResponseEntity.ok(serviceMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
