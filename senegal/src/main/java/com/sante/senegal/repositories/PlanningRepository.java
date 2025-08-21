package com.sante.senegal.repositories;

import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PlanningRepository extends JpaRepository<Planning, Long> {

    // Correction : utiliser LocalDate au lieu de LocalDateTime
    boolean existsByMedecinAndDateAndHeureDebut(Medecin medecin, LocalDate date, LocalTime heureDebut);

    // Correction : utiliser LocalDate pour les dates
    List<Planning> findByMedecinIdAndDateBetween(Long medecin_id, LocalDate dateDebut, LocalDate dateFin);

    List<Planning> findByMedecinIdAndDateAndReserve(Long medecin_id, LocalDate date, boolean reserve);

    /**
     * Trouve les créneaux disponibles avec critères multiples
     */
    @Query("SELECT p FROM Planning p " +
            "WHERE (:idMedecin IS NULL OR p.medecin.id = :idMedecin) " +
            "AND (:idService IS NULL OR p.service.idService = :idService) " +
            "AND (:idHopital IS NULL OR p.hopital.idHopital = :idHopital) " +
            "AND p.date BETWEEN :dateDebut AND :dateFin " +
            "AND p.reserve = false " +
            "ORDER BY p.date ASC, p.heureDebut ASC")
    List<Planning> findCreneauxDisponibles(@Param("idMedecin") Long idMedecin,
                                           @Param("idService") Long idService,
                                           @Param("idHopital") Long idHopital,
                                           @Param("dateDebut") LocalDate dateDebut,
                                           @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les créneaux disponibles par critères pour une date spécifique
     */
    @Query("SELECT p FROM Planning p " +
            "WHERE (:idMedecin IS NULL OR p.medecin.id = :idMedecin) " +
            "AND (:idService IS NULL OR p.service.idService = :idService) " +
            "AND (:idHopital IS NULL OR p.hopital.idHopital = :idHopital) " +
            "AND p.date = :date " +
            "AND p.reserve = false " +
            "ORDER BY p.heureDebut ASC")
    List<Planning> findCreneauxDisponiblesParCriteres(@Param("idMedecin") Long idMedecin,
                                                      @Param("idService") Long idService,
                                                      @Param("idHopital") Long idHopital,
                                                      @Param("date") LocalDate date);

    /**
     * Trouve les créneaux disponibles par médecin sur une période
     */
    List<Planning> findByMedecinIdAndDateBetweenAndReserve(Long medecin_id, LocalDate dateDebut, LocalDate dateFin, boolean reserve);

    /**
     * Trouve les créneaux disponibles par service sur une période
     * Correction : utiliser le bon nom de propriété pour l'ID du service
     */
    List<Planning> findByServiceIdServiceAndDateBetweenAndReserve(Long service_idService, LocalDate dateDebut, LocalDate dateFin, boolean reserve);

    /**
     * Trouve les créneaux disponibles par hôpital sur une période
     * Correction : utiliser le bon nom de propriété pour l'ID de l'hôpital
     */
    List<Planning> findByHopitalIdHopitalAndDateBetweenAndReserve(Long hopital_idHopital, LocalDate dateDebut, LocalDate dateFin, boolean reserve);

    /**
     * Compte les créneaux disponibles pour un médecin à une date donnée
     */
    long countByMedecinIdAndDateAndReserve(Long medecin_id, LocalDate date, boolean reserve);

    /**
     * Trouve un planning par rendez-vous
     */
    Optional<Planning> findByRendezVousIdRdv(Long idRdv);

    /**
     * Vérifie l'existence d'un planning par médecin, date et heure
     */
    boolean existsByMedecinIdAndDateAndHeureDebutAndReserve(Long medecin_id, LocalDate date, LocalTime heureDebut, boolean reserve);

    /**
     * Trouve les plannings par médecin, date et heure avec statut de réservation
     */
    List<Planning> findByMedecinIdAndDateAndHeureDebutAndReserve(Long medecin_id, LocalDate date, LocalTime heureDebut, boolean reserve);
}