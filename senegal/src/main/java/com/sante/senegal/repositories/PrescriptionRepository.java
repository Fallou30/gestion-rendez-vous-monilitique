package com.sante.senegal.repositories;

import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repository pour Prescription
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByConsultationDossierPatientIdAndStatut(Long patientId,
                                                                   Prescription.StatutPrescription statut);

    @Query("SELECT p FROM Prescription p WHERE p.consultation.idConsultation = :consultationId")
    List<Prescription> findByConsultationId(@Param("consultationId") Long consultationId);

    List<Prescription> findByDatePrescriptionBetween(LocalDate dateDebut, LocalDate dateFin);

    List<Prescription> findByStatut(Prescription.StatutPrescription statut);

    @Query("SELECT p FROM Prescription p WHERE p.consultation.rendezVous.medecin.id = :medecinId AND p.statut = :statut")
    List<Prescription> findByMedecinIdAndStatut(@Param("medecinId") Long medecinId,
                                                @Param("statut") Prescription.StatutPrescription statut);

    @Query("SELECT p FROM Prescription p WHERE p.consultation.dossier.patient.id = :patientId ORDER BY p.datePrescription DESC")
    List<Prescription> findByPatientIdOrderByDatePrescriptionDesc(@Param("patientId") Long patientId);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.consultation.rendezVous.medecin.id = :medecinId AND p.datePrescription BETWEEN :dateDebut AND :dateFin")
    Long countByMedecinIdAndDatePrescriptionBetween(@Param("medecinId") Long medecinId,
                                                    @Param("dateDebut") LocalDate dateDebut,
                                                    @Param("dateFin") LocalDate dateFin);

    Prescription findByConsultation(Consultation consultation);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.consultation = :consultation")
    int countByConsultation(@Param("consultation") Consultation consultation);

    // Correction de countPrescriptionsParMedecin
    @Query("SELECT COUNT(p) FROM Prescription p " +
            "WHERE p.consultation.rendezVous.medecin.id = :medecinId " +
            "AND p.datePrescription BETWEEN :dateDebut AND :dateFin")
    int countPrescriptionsParMedecin(@Param("dateDebut") LocalDate dateDebut,
                                     @Param("dateFin") LocalDate dateFin);
}
