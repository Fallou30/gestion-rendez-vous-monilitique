package com.sante.senegal.repositories;

import com.sante.senegal.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Repository pour Consultation
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByDossierPatientIdOrderByDateHeureDesc(Long patientId);

    List<Consultation> findByRendezVousMedecinIdAndDateHeureBetween(Long medecinId,
                                                                    LocalDateTime dateDebut,
                                                                    LocalDateTime dateFin);

    List<Consultation> findByStatut(Consultation.StatutConsultation statut);

    @Query("SELECT c FROM Consultation c WHERE c.rendezVous.medecin.id = :medecinId AND c.statut = :statut")
    List<Consultation> findByMedecinIdAndStatut(@Param("medecinId") Long medecinId,
                                                @Param("statut") Consultation.StatutConsultation statut);

    @Query("SELECT c FROM Consultation c WHERE c.dossier.patient.id = :patientId AND c.dateHeure >= :dateDebut AND c.dateHeure <= :dateFin")
    List<Consultation> findByPatientIdAndDateHeureBetween(@Param("patientId") Long patientId,
                                                          @Param("dateDebut") LocalDateTime dateDebut,
                                                          @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.rendezVous.medecin.id = :medecinId AND c.statut = :statut")
    Long countByMedecinIdAndStatut(@Param("medecinId") Long medecinId,
                                   @Param("statut") Consultation.StatutConsultation statut);

    @Query("SELECT c FROM Consultation c WHERE c.dateHeure BETWEEN :dateDebut AND :dateFin ORDER BY c.dateHeure DESC")
    List<Consultation> findByDateHeureBetweenOrderByDateHeureDesc(@Param("dateDebut") LocalDateTime dateDebut,
                                                                  @Param("dateFin") LocalDateTime dateFin);
    @Query("SELECT c FROM Consultation c " +
            "JOIN c.rendezVous r " +
            "JOIN r.patient p " +
            "WHERE p.id = :patientId " +
            "AND c.dateHeure BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY c.dateHeure DESC")
    List<Consultation> findByPatientIdBetweenDates(
            @Param("patientId") Long patientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    List<Consultation> findByDossierPatientIdAndDateHeureBetween(Long patientId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT FUNCTION('DATE', c.dateHeure) as dateConsultation, COUNT(c) as total " +
            "FROM Consultation c " +
            "WHERE FUNCTION('DATE', c.dateHeure) BETWEEN :dateDebut AND :dateFin " +
            "GROUP BY FUNCTION('DATE', c.dateHeure)")
    List<Object[]> countConsultationsParJour(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    List<Consultation> findByDiagnosticContainingIgnoreCase(String terme);

    List<Consultation> findByDateHeureBetween(LocalDateTime dateHeureAfter, LocalDateTime dateHeureBefore);
    @Query("SELECT c FROM Consultation c JOIN c.rendezVous r WHERE r.patient.id = :patientId ORDER BY c.dateHeure DESC")
    List<Consultation> findByPatientIdOrderByDateDesc(@Param("patientId") Long patientId);
}


