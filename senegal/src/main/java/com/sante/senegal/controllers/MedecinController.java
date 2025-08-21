package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Hopital;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.services.interfaces.MedecinService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class MedecinController {

    private final MedecinService medecinService;
    private final ModelMapper modelMapper;

    // ===============================
    // Demande d’inscription médecin
    // ===============================
    @PostMapping("/medecin/demande")
    public ResponseEntity<?> demandeInscriptionMedecin(
            @ModelAttribute @Valid DemandeMedecinDto dto,
            @RequestParam(required = false) MultipartFile cv,
            @RequestParam(required = false) MultipartFile diplome,
            @RequestParam(required = false) MultipartFile carteOrdre
    ) {
        try {
            log.info("Création médecin par admin: {}", dto.getEmail());
            AuthResponseDto response = medecinService.demanderInscriptionMedecin(dto, cv, diplome, carteOrdre);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InscriptionException e) {
            log.warn("Erreur lors de la création du médecin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du médecin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }

    // ===============================
    // Obtenir un médecin par ID
    // ===============================
    @GetMapping("/medecin/{id}")
    public ResponseEntity<MedecinDto> getMedecinById(@PathVariable Long id) {
        try {
            Medecin medecin = medecinService.getMedecinById(id);
            MedecinDto dto = modelMapper.map(medecin, MedecinDto.class);

            dto.setIdsHopitaux(medecin.getHopitaux().stream()
                    .map(Hopital::getIdHopital)
                    .toList());

            dto.setIdService(medecin.getService() != null ? medecin.getService().getIdService() : null);

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===============================
    // Vérification email médecin
    // ===============================
    @GetMapping("/public/medecin/email-existe")
    public ResponseEntity<Boolean> emailExiste(@RequestParam String email) {
        boolean exists = medecinService.emailExiste(email);
        return ResponseEntity.ok(exists);
    }
    @PutMapping("/medecin/{id}/profil")
    public ResponseEntity<UtilisateurDetailDto> modifierProfil(@PathVariable Long id,
                                                               @RequestBody UtilisateurDetailDto dto) {
        try {
            UtilisateurDetailDto updated = medecinService.modifierProfil(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // ===============================
    // Consultation profil par email
    // ===============================
    @GetMapping("/profil")
    public ResponseEntity<UtilisateurDetailDto> consulterProfil(@RequestParam String email) {
        UtilisateurDetailDto profil = medecinService.consulterProfil(email);
        return ResponseEntity.ok(profil);
    }


    // ===============================
    // Utils
    // ===============================
    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> erreur = new HashMap<>();
        erreur.put("erreur", message);
        return erreur;
    }
    // Get doctors by service
    @GetMapping("/medecins/service/{serviceId}")
    public ResponseEntity<List<MedecinByServiceDto>> getByService(@PathVariable Long serviceId) {
        try {
            List<MedecinByServiceDto> medecins = medecinService.getMedecinsByService(serviceId);
            return ResponseEntity.ok(medecins);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get doctors by hospital
    @GetMapping("/medecins/hopital/{hopitalId}")
    public ResponseEntity<List<MedecinByHopitalDto>> getByHopital(@PathVariable Long hopitalId) {
        try {
            List<MedecinByHopitalDto> medecins = medecinService.getMedecinsByHopital(hopitalId);
            return ResponseEntity.ok(medecins);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get doctors by both service and hospital
    @GetMapping("/medecins/service/{serviceId}/hopital/{hopitalId}")
    public ResponseEntity<List<MedecinDto>> getByServiceAndHopital(
            @PathVariable Long serviceId,
            @PathVariable Long hopitalId) {
        try {
            List<MedecinDto> medecins = medecinService.getMedecinsByServiceAndHopital(serviceId, hopitalId);
            return ResponseEntity.ok(medecins);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
