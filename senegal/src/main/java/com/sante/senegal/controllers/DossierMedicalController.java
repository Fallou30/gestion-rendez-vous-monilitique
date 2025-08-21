package com.sante.senegal.controllers;

import com.sante.senegal.dto.CreateDossierRequest;
import com.sante.senegal.dto.DocumentMedicalDto;
import com.sante.senegal.dto.DossierMedicalDto;
import com.sante.senegal.dto.UpdateDossierRequest;
import com.sante.senegal.exceptions.FileStorageException;
import com.sante.senegal.services.interfaces.DossierMedicalService;
import com.sante.senegal.services.interfaces.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/dossiers-medicaux")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DossierMedicalController {

    private final DossierMedicalService dossierService;
    private final FileStorageService fileStorageService;

    /**
     * Créer un dossier médical
     */
    @PostMapping
    public ResponseEntity<DossierMedicalDto> creerDossier(@RequestBody CreateDossierRequest request) {
        try {
            DossierMedicalDto dossier = dossierService.creerDossierMedical(request);
            return ResponseEntity.ok(dossier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mettre à jour un dossier médical
     */
    @PutMapping("/{id}")
    public ResponseEntity<DossierMedicalDto> mettreAJourDossier(@PathVariable Long id,
                                                                @RequestBody UpdateDossierRequest request) {
        try {
            // Il faut que UpdateDossierRequest soit compatible avec DossierMedicalDto ou faire une conversion ici
            DossierMedicalDto dto = new DossierMedicalDto();
            dto.setAntecedentsMedicaux(request.getAntecedentsMedicaux());
            dto.setAntecedentsFamiliaux(request.getAntecedentsFamiliaux());
            dto.setVaccinations(request.getVaccinations());
            dto.setNotesGenerales(request.getNotesGenerales());

            DossierMedicalDto dossier = dossierService.mettreAJourDossier(id, dto);
            return ResponseEntity.ok(dossier);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer le dossier d'un patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<DossierMedicalDto> getDossierPatient(@PathVariable Long patientId) {
        try {
            DossierMedicalDto dossier = dossierService.getDossierParPatient(patientId);
            return ResponseEntity.ok(dossier);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{dossierId}/documents")
    public ResponseEntity<?> ajouterDocument(
            @PathVariable Long dossierId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("typeDocument") String typeDocument,
            @RequestParam("description") String description) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier ne peut pas être vide");
            }

            String storedFilePath = fileStorageService.storeFile(file, "medical_documents");

            var document = dossierService.ajouterDocument(
                    dossierId,
                    typeDocument,
                    file.getOriginalFilename(),
                    storedFilePath,
                    file.getSize(),
                    description
            );

            return ResponseEntity.ok(document);

        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur de stockage : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur de traitement : " + e.getMessage());
        }
    }

    /**
     * Archiver un dossier médical
     */
    @PutMapping("/{id}/archiver")
    public ResponseEntity<Void> archiverDossier(@PathVariable Long id) {
        try {
            dossierService.archiverDossier(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer les documents associés à un dossier
     */
    @GetMapping("/{dossierId}/documents")
    public ResponseEntity<List<DocumentMedicalDto>> getDocumentsParDossier(@PathVariable Long dossierId) {
        try {
            List<DocumentMedicalDto> documents = dossierService.getDocumentsParDossier(dossierId);
            return ResponseEntity.ok(documents);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/patient/{patientId}/documents")
    public ResponseEntity<List<DocumentMedicalDto>> getDocumentsParPatient(@PathVariable Long patientId) {
        try {
            List<DocumentMedicalDto> documents = dossierService.getDocumentsParPatient(patientId);
            return ResponseEntity.ok(documents);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patient/{patientId}/documents/{documentId}")
    public ResponseEntity<DocumentMedicalDto> getDocumentParPatient(@PathVariable Long patientId,
                                                                    @PathVariable Long documentId) {
        try {
            DocumentMedicalDto document = dossierService.getDocumentParPatient(patientId, documentId);
            return ResponseEntity.ok(document);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
