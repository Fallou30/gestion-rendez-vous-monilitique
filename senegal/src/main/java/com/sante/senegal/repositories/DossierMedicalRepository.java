package com.sante.senegal.repositories;

import com.sante.senegal.entities.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Repository pour DossierMedical
@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {

    Optional<DossierMedical> findByPatientId(Long patientId);

    List<DossierMedical> findByStatut(DossierMedical.StatutDossier statut);

    @Query("SELECT d FROM DossierMedical d WHERE d.patient.id = :patientId AND d.statut = :statut")
    Optional<DossierMedical> findByPatientIdAndStatut(@Param("patientId") Long patientId,
                                                      @Param("statut") DossierMedical.StatutDossier statut);

    @Query("SELECT COUNT(d) FROM DossierMedical d WHERE d.statut = :statut")
    Long countByStatut(@Param("statut") DossierMedical.StatutDossier statut);

    List<DossierMedical> findByDateCreationBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
}

