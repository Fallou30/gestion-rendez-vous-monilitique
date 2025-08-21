package com.sante.senegal.repositories;

import com.sante.senegal.entities.DocumentMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository pour DocumentMedical
@Repository
public interface DocumentMedicalRepository extends JpaRepository<DocumentMedical, Long> {

    List<DocumentMedical> findByDossierIdDossier(Long dossierId);

    List<DocumentMedical> findByDossierIdDossierAndStatut(Long dossierId, DocumentMedical.StatutDocument statut);
    List<DocumentMedical> findByTypeDocumentAndStatut(String typeDocument, DocumentMedical.StatutDocument statut);

    @Query("SELECT d FROM DocumentMedical d WHERE d.dossier.patient.id = :patientId AND d.statut = :statut")
    List<DocumentMedical> findByPatientIdAndStatut(@Param("patientId") Long patientId,
                                                   @Param("statut") DocumentMedical.StatutDocument statut);

    @Query("SELECT d FROM DocumentMedical d WHERE d.dossier.idDossier = :dossierId AND d.typeDocument = :typeDocument")
    List<DocumentMedical> findByDossierIdAndTypeDocument(@Param("dossierId") Long dossierId,
                                                         @Param("typeDocument") String typeDocument);
    Optional<DocumentMedical> findByIdDocumentAndDossierIdDossier(Long id, Long dossierId);


}

