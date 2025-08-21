package com.sante.senegal.repositories;

import com.sante.senegal.dto.MedicamentPrescritStat;
import com.sante.senegal.entities.MedicamentPrescrit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository pour MedicamentPrescrit
@Repository
public interface MedicamentPrescritRepository extends JpaRepository<MedicamentPrescrit, Long> {

    List<MedicamentPrescrit> findByIdMedicamentPrescrit(Long prescriptionId);

    List<MedicamentPrescrit> findByStatut(MedicamentPrescrit.StatutMedicament statut);

    @Query("SELECT m FROM MedicamentPrescrit m WHERE m.prescription.consultation.dossier.patient.id = :patientId AND m.statut = :statut")
    List<MedicamentPrescrit> findByPatientIdAndStatut(@Param("patientId") Long patientId,
                                                      @Param("statut") MedicamentPrescrit.StatutMedicament statut);

    @Query("SELECT m FROM MedicamentPrescrit m WHERE m.prescription.consultation.dossier.patient.id = :patientId ORDER BY m.prescription.datePrescription DESC")
    List<MedicamentPrescrit> findByPatientIdOrderByDatePrescriptionDesc(@Param("patientId") Long patientId);

    // Requête pour les statistiques des médicaments les plus prescrits
    @Query(value = "SELECT new com.sante.senegal.dto.MedicamentPrescritStat(m.nomMedicament, COUNT(m)) " +
            "FROM MedicamentPrescrit m " +
            "GROUP BY m.nomMedicament " +
            "ORDER BY COUNT(m) DESC")
    List<MedicamentPrescritStat> findTopMedicamentsPrescrits(Pageable pageable);

    @Query("SELECT COUNT(m) FROM MedicamentPrescrit m WHERE m.nomMedicament = :nomMedicament AND m.statut = :statut")
    Long countByNomMedicamentAndStatut(@Param("nomMedicament") String nomMedicament,
                                       @Param("statut") MedicamentPrescrit.StatutMedicament statut);

    List<MedicamentPrescrit> findByNomMedicamentContainingIgnoreCase(String nomMedicament);
}

