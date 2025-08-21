package com.sante.senegal.repositories;

import com.sante.senegal.entities.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByPatientId(Long idPatient);

    List<RendezVous> findByMedecinId(Long idMedecin);

    List<RendezVous> findByServiceIdService(Long idService);

    List<RendezVous> findByHopitalIdHopital(Long idHopital);

    List<RendezVous> findByStatut(RendezVous.StatutRendezVous statut);
    List<RendezVous> findByMedecinIdAndStatut(Long idMedecin, RendezVous.StatutRendezVous statut);

    List<RendezVous> findByNiveauUrgence(RendezVous.NiveauUrgence niveauUrgence);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :dateDebut AND :dateFin")
    List<RendezVous> findRendezVousBetweenDates(@Param("dateDebut") LocalDateTime dateDebut,
                                                @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :idMedecin AND r.dateHeure BETWEEN :dateDebut AND :dateFin")
    List<RendezVous> findByMedecinAndDateRange(@Param("idMedecin") Long idMedecin,
                                               @Param("dateDebut") LocalDateTime dateDebut,
                                               @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :idPatient AND r.statut IN ('PROGRAMME', 'CONFIRME')")
    List<RendezVous> findUpcomingRendezVousByPatient(@Param("idPatient") Long idPatient);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure < :maintenant AND r.statut = 'PROGRAMME'")
    List<RendezVous> findOverdueRendezVous(@Param("maintenant") LocalDateTime maintenant);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin.id = :idMedecin AND r.dateHeure BETWEEN :dateDebut AND :dateFin")
    Long countRendezVousByMedecinAndDate(@Param("idMedecin") Long idMedecin,
                                         @Param("dateDebut") LocalDateTime dateDebut,
                                         @Param("dateFin") LocalDateTime dateFin);
    // Ajout dans RendezVousRepository.java
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId " +
            "AND r.dateHeure BETWEEN :dateDebut AND :dateFin " +
            "AND r.statut NOT IN ('ANNULE', 'REPORTE')")
    List<RendezVous> findByMedecinAndDateBetween(
            @Param("medecinId") Long medecinId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :idMedecin AND r.dateHeure > CURRENT_TIMESTAMP AND r.statut IN ('PROGRAMME', 'CONFIRME')")
    List<RendezVous> findUpcomingRendezVousByMedecin(@Param("idMedecin") Long idMedecin);


}