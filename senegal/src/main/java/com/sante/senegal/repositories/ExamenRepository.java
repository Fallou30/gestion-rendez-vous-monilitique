package com.sante.senegal.repositories;

import com.sante.senegal.dto.ExamenStatistiqueDto;
import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Repository pour Examen
@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByConsultationDossierPatientIdOrderByDatePrescriptionDesc(Long patientId);

   // List<Examen> findByConsultationId(Long consultationId);

    List<Examen> findByStatut(Examen.StatutExamen statut);

    List<Examen> findByStatutIn(List<Examen.StatutExamen> statuts);

    List<Examen> findByUrgenceInAndStatutNot(List<Examen.NiveauUrgence> urgences,
                                             Examen.StatutExamen statut);

    List<Examen> findByTypeExamenAndStatut(String typeExamen, Examen.StatutExamen statut);

    @Query("SELECT e FROM Examen e WHERE e.consultation.rendezVous.medecin.id = :medecinId AND e.statut = :statut")
    List<Examen> findByMedecinIdAndStatut(@Param("medecinId") Long medecinId,
                                          @Param("statut") Examen.StatutExamen statut);

    @Query("SELECT e FROM Examen e WHERE e.dateRealisation = :date AND e.statut = :statut")
    List<Examen> findByDateRealisationAndStatut(@Param("date") LocalDate date,
                                                @Param("statut") Examen.StatutExamen statut);

    @Query("SELECT e FROM Examen e WHERE e.datePrescription BETWEEN :dateDebut AND :dateFin ORDER BY e.datePrescription DESC")
    List<Examen> findByDatePrescriptionBetweenOrderByDatePrescriptionDesc(@Param("dateDebut") LocalDate dateDebut,
                                                                          @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(e) FROM Examen e WHERE e.consultation.rendezVous.medecin.id = :medecinId AND e.statut = :statut")
    Long countByMedecinIdAndStatut(@Param("medecinId") Long medecinId,
                                   @Param("statut") Examen.StatutExamen statut);

    // Examens en retard (prescrits mais pas encore programmés après X jours)
    @Query("SELECT e FROM Examen e WHERE e.statut = :statut AND e.datePrescription <= :dateLimit")
    List<Examen> findExamensEnRetard(@Param("statut") Examen.StatutExamen statut,
                                     @Param("dateLimit") LocalDate dateLimit);

    // Statistiques par type d'examen
    // Requête native avec vérification de sécurité
    @Query("SELECT new com.sante.senegal.dto.ExamenStatistiqueDto(e.typeExamen, COUNT(e)) " +
            "FROM Examen e " +
            "WHERE e.datePrescription BETWEEN :dateDebut AND :dateFin " +
            "GROUP BY e.typeExamen " +
            "ORDER BY COUNT(e) DESC")
    List<ExamenStatistiqueDto> getStatistiquesParTypeExamen(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    // Méthodes corrigées
    Optional<Examen> findByConsultation(Consultation consultation);
    @Query("SELECT COUNT(e) FROM Examen e WHERE e.datePrescription BETWEEN :dateDebut AND :dateFin")
    int countBetweenDates(@Param("dateDebut") LocalDate dateDebut,
                          @Param("dateFin") LocalDate dateFin);

    //int countExamensParType(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT COUNT(e) FROM Examen e WHERE e.consultation = :consultation")
    int countByConsultation(@Param("consultation") Consultation consultation);
}