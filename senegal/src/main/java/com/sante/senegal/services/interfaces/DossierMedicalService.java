package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.CreateDossierRequest;
import com.sante.senegal.dto.DocumentMedicalDto;
import com.sante.senegal.dto.DossierMedicalDto;
import com.sante.senegal.entities.DocumentMedical;

import java.util.List;

public interface DossierMedicalService {
    DossierMedicalDto creerDossierMedical(CreateDossierRequest request);
    DossierMedicalDto mettreAJourDossier(Long dossierId, DossierMedicalDto dto);
    DossierMedicalDto getDossierParPatient(Long patientId);
    void archiverDossier(Long dossierId);
    List<DocumentMedicalDto> getDocumentsParDossier(Long dossierId);
    DocumentMedical ajouterDocument(Long dossierId, String typeDocument,
                                              String nomFichier, String cheminFichier,
                                              Long tailleFichier, String description);
    List<DocumentMedicalDto> getDocumentsParPatient(Long patientId);
    DocumentMedicalDto getDocumentParPatient(Long patientId, Long documentId);

}
