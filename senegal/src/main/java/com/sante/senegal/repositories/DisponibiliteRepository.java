package com.sante.senegal.repositories;

import com.sante.senegal.entities.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    // Recherches par entité unique
    List<Disponibilite> findByMedecinId(Long idMedecin);
    List<Disponibilite> findByServiceIdService(Long idService);
    List<Disponibilite> findByHopitalIdHopital(Long idHopital);
    List<Disponibilite> findByDate(LocalDate date);
    List<Disponibilite> findByStatut(Disponibilite.StatutDisponibilite statut);

    // Recherches combinées
    List<Disponibilite> findByDateAndStatut(LocalDate date, Disponibilite.StatutDisponibilite statut);

    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date = :date AND d.statut = :statut")
    List<Disponibilite> findByIdMedecinAndDateAndStatut(@Param("idMedecin") Long idMedecin,
                                                        @Param("date") LocalDate date,
                                                        @Param("statut") Disponibilite.StatutDisponibilite statut);

    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date = :date AND d.statut = :statut")
    List<Disponibilite> findByMedecinDateAndStatut(@Param("idMedecin") Long idMedecin,
                                                   @Param("date") LocalDate date,
                                                   @Param("statut") Disponibilite.StatutDisponibilite statut);

    @Query("SELECT d FROM Disponibilite d WHERE d.service.idService = :idService AND d.date = :date AND d.statut = :statut")
    List<Disponibilite> findByServiceDateAndStatut(@Param("idService") Long idService,
                                                   @Param("date") LocalDate date,
                                                   @Param("statut") Disponibilite.StatutDisponibilite statut);

    // Recherches par période
    @Query("SELECT d FROM Disponibilite d WHERE d.date BETWEEN :dateDebut AND :dateFin AND d.statut = 'DISPONIBLE'")
    List<Disponibilite> findAvailableDisponibilitesBetweenDates(@Param("dateDebut") LocalDate dateDebut,
                                                                @Param("dateFin") LocalDate dateFin);

    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date BETWEEN :dateDebut AND :dateFin")
    List<Disponibilite> findByMedecinBetweenDates(@Param("idMedecin") Long idMedecin,
                                                  @Param("dateDebut") LocalDate dateDebut,
                                                  @Param("dateFin") LocalDate dateFin);

    @Query("SELECT d FROM Disponibilite d WHERE d.service.idService = :idService AND d.date BETWEEN :dateDebut AND :dateFin")
    List<Disponibilite> findByServiceBetweenDates(@Param("idService") Long idService,
                                                  @Param("dateDebut") LocalDate dateDebut,
                                                  @Param("dateFin") LocalDate dateFin);

    @Query("SELECT d FROM Disponibilite d WHERE d.hopital.idHopital = :idHopital AND d.date BETWEEN :dateDebut AND :dateFin")
    List<Disponibilite> findByHopitalBetweenDates(@Param("idHopital") Long idHopital,
                                                  @Param("dateDebut") LocalDate dateDebut,
                                                  @Param("dateFin") LocalDate dateFin);

    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date BETWEEN :dateDebut AND :dateFin AND d.statut = :statut")
    List<Disponibilite> findByMedecinBetweenDatesAndStatut(@Param("idMedecin") Long idMedecin,
                                                           @Param("dateDebut") LocalDate dateDebut,
                                                           @Param("dateFin") LocalDate dateFin,
                                                           @Param("statut") Disponibilite.StatutDisponibilite statut);

    // Recherche de conflits
    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date = :date " +
            "AND ((d.heureDebut < :heureFin AND d.heureFin > :heureDebut)) " +
            "AND d.statut != 'INDISPONIBLE'")
    List<Disponibilite> findConflictingDisponibilites(@Param("idMedecin") Long idMedecin,
                                                      @Param("date") LocalDate date,
                                                      @Param("heureDebut") LocalTime heureDebut,
                                                      @Param("heureFin") LocalTime heureFin);

    // Vérification de disponibilité
    @Query("SELECT d FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date = :date " +
            "AND d.heureDebut <= :heureDebut AND d.heureFin >= :heureFin " +
            "AND d.statut = 'DISPONIBLE'")
    Optional<Disponibilite> verifierDisponibilite(@Param("idMedecin") Long idMedecin,
                                                  @Param("date") LocalDate date,
                                                  @Param("heureDebut") LocalTime heureDebut,
                                                  @Param("heureFin") LocalTime heureFin);

    // Recherche avec critères multiples
    @Query("SELECT d FROM Disponibilite d WHERE " +
            "(:idMedecin IS NULL OR d.medecin.id = :idMedecin) AND " +
            "(:idService IS NULL OR d.service.idService = :idService) AND " +
            "(:idHopital IS NULL OR d.hopital.idHopital = :idHopital) AND " +
            "(:dateDebut IS NULL OR d.date >= :dateDebut) AND " +
            "(:dateFin IS NULL OR d.date <= :dateFin) AND " +
            "(:statut IS NULL OR d.statut = :statut) " +
            "ORDER BY d.date, d.heureDebut")
    List<Disponibilite> findByMultipleCriteria(@Param("idMedecin") Long idMedecin,
                                               @Param("idService") Long idService,
                                               @Param("idHopital") Long idHopital,
                                               @Param("dateDebut") LocalDate dateDebut,
                                               @Param("dateFin") LocalDate dateFin,
                                               @Param("statut") Disponibilite.StatutDisponibilite statut);

    // Statistiques
    @Query("SELECT COUNT(d) FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date BETWEEN :dateDebut AND :dateFin")
    Long countByMedecinBetweenDates(@Param("idMedecin") Long idMedecin,
                                    @Param("dateDebut") LocalDate dateDebut,
                                    @Param("dateFin") LocalDate dateFin);

    @Query("SELECT COUNT(d) FROM Disponibilite d WHERE d.medecin.id = :idMedecin AND d.date BETWEEN :dateDebut AND :dateFin AND d.statut = :statut")
    Long countByMedecinBetweenDatesAndStatut(@Param("idMedecin") Long idMedecin,
                                             @Param("dateDebut") LocalDate dateDebut,
                                             @Param("dateFin") LocalDate dateFin,
                                             @Param("statut") Disponibilite.StatutDisponibilite statut);

    // Nettoyage des données
    @Query("DELETE FROM Disponibilite d WHERE d.date < :date")
    void deleteOldDisponibilites(@Param("date") LocalDate date);

    // Recherche des disponibilités futures
    @Query("SELECT d FROM Disponibilite d WHERE d.date > CURRENT_DATE AND d.statut = 'DISPONIBLE' ORDER BY d.date, d.heureDebut")
    List<Disponibilite> findFutureAvailableDisponibilites();

    // Recherche par jour de la semaine
    @Query("SELECT d FROM Disponibilite d WHERE d.jourSemaine = :jourSemaine AND d.statut = 'DISPONIBLE'")
    List<Disponibilite> findByJourSemaineAndStatutDisponible(@Param("jourSemaine") java.time.DayOfWeek jourSemaine);
}