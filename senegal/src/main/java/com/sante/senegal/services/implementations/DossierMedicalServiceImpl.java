package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.CreateDossierRequest;
import com.sante.senegal.dto.DocumentMedicalDto;
import com.sante.senegal.dto.DossierMedicalDto;
import com.sante.senegal.entities.DocumentMedical;
import com.sante.senegal.entities.DossierMedical;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.repositories.DocumentMedicalRepository;
import com.sante.senegal.repositories.DossierMedicalRepository;
import com.sante.senegal.repositories.PatientRepository;
import com.sante.senegal.services.interfaces.DossierMedicalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierRepository;
    private final PatientRepository patientRepository;
    private final DocumentMedicalRepository documentMedicalRepository;
    private final ModelMapper modelMapper;

    @Override
    public DossierMedicalDto creerDossierMedical(CreateDossierRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé"));

        if (patient.getDossierMedical() != null) {
            throw new IllegalStateException("Ce patient a déjà un dossier médical");
        }

        DossierMedical dossier = DossierMedical.builder()
                .patient(patient)
                .antecedentsMedicaux(request.getAntecedentsMedicaux())
                .antecedentsFamiliaux(request.getAntecedentsFamiliaux())
                .vaccinations(request.getVaccinations())
                .statut(DossierMedical.StatutDossier.ACTIF)
                .build();

        return modelMapper.map(dossierRepository.save(dossier), DossierMedicalDto.class);
    }

    @Override
    public DossierMedicalDto mettreAJourDossier(Long dossierId, DossierMedicalDto dto) {
        DossierMedical dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé"));

        if (dto.getAntecedentsMedicaux() != null) dossier.setAntecedentsMedicaux(dto.getAntecedentsMedicaux());
        if (dto.getAntecedentsFamiliaux() != null) dossier.setAntecedentsFamiliaux(dto.getAntecedentsFamiliaux());
        if (dto.getVaccinations() != null) dossier.setVaccinations(dto.getVaccinations());
        if (dto.getNotesGenerales() != null) dossier.setNotesGenerales(dto.getNotesGenerales());

        return modelMapper.map(dossierRepository.save(dossier), DossierMedicalDto.class);
    }

    @Override
    public DossierMedicalDto getDossierParPatient(Long patientId) {
        DossierMedical dossier = dossierRepository.findByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé pour ce patient"));
        return modelMapper.map(dossier, DossierMedicalDto.class);
    }
    /**
     * Ajouter un document médical
     */

    public DocumentMedical ajouterDocument(Long dossierId, String typeDocument,
                                              String nomFichier, String cheminFichier,
                                              Long tailleFichier, String description) {
        DossierMedical dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé"));

        DocumentMedical document = DocumentMedical.builder()
                .dossier(dossier)
                .typeDocument(typeDocument)
                .nomFichier(nomFichier)
                .cheminFichier(cheminFichier)
                .tailleFichier(tailleFichier)
                .description(description)
                .statut(DocumentMedical.StatutDocument.ACTIF)
                .build();

        return documentMedicalRepository.save(document);
    }
    public List<DocumentMedicalDto> getDocumentsParPatient(Long patientId) {
        DossierMedical dossier = dossierRepository.findByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable pour ce patient"));

        return documentMedicalRepository.findByDossierIdDossier(dossier.getIdDossier()).stream()
                .map(doc -> modelMapper.map(doc, DocumentMedicalDto.class))
                .collect(Collectors.toList());
    }

    public DocumentMedicalDto getDocumentParPatient(Long patientId, Long documentId) {
        DossierMedical dossier = dossierRepository.findByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable pour ce patient"));

        DocumentMedical document = documentMedicalRepository.findByIdDocumentAndDossierIdDossier(documentId, dossier.getIdDossier())
                .orElseThrow(() -> new EntityNotFoundException("Document introuvable pour ce patient"));

        return modelMapper.map(document, DocumentMedicalDto.class);
    }
    @Override
    public void archiverDossier(Long dossierId) {
        DossierMedical dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé"));

        dossier.setStatut(DossierMedical.StatutDossier.ARCHIVE);
        dossierRepository.save(dossier);
    }

    @Override
    public List<DocumentMedicalDto> getDocumentsParDossier(Long dossierId) {
        DossierMedical dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé"));

        return dossier.getDocuments().stream()
                .map(document -> modelMapper.map(document, DocumentMedicalDto.class))
                .collect(Collectors.toList());
    }

}
