package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.PlanningDto;
import com.sante.senegal.dto.PlanningReservationRequestDto;
import com.sante.senegal.entities.Planning;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanningService {

    /**
     * Génère les plannings pour un médecin dans un hôpital pour le mois suivant
     */
    void genererPlanningsPourUnMois(Long idMedecin, Long idHopital);

    /**
     * Récupère les plannings d'un médecin sur une période donnée
     * Correction : utiliser LocalDate pour la cohérence
     */
    List<Planning> getPlanningsByMedecinAndPeriode(Long idMedecin, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les créneaux disponibles pour un médecin à une date donnée
     */
    List<Planning> getCreneauxDisponibles(Long idMedecin, LocalDate date);

    /**
     * Récupère les créneaux disponibles avec critères multiples (DTO enrichi)
     */
    List<PlanningDto> getCreneauxDisponiblesDto(Long idMedecin, Long idService, Long idHopital, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les créneaux disponibles par critères pour une date spécifique
     */
    List<PlanningDto> getCreneauxDisponiblesParCriteres(Long idMedecin, Long idService, Long idHopital, LocalDate date);

    /**
     * Réserve un créneau pour un patient
     */
    PlanningDto reserverCreneau(PlanningReservationRequestDto request);

    /**
     * Libère un créneau réservé
     */
    PlanningDto libererCreneau(Long idPlanning);

    /**
     * Récupère les créneaux disponibles par médecin sur une période
     */
    List<PlanningDto> getCreneauxDisponiblesParMedecin(Long idMedecin, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les créneaux disponibles par service sur une période
     */
    List<PlanningDto> getCreneauxDisponiblesParService(Long idService, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les créneaux disponibles par hôpital sur une période
     */
    List<PlanningDto> getCreneauxDisponiblesParHopital(Long idHopital, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Vérifie si un créneau est disponible
     */
    boolean isCreneauDisponible(Long idPlanning);

    /**
     * Compte le nombre de créneaux disponibles pour un médecin à une date donnée
     * Correction : utiliser LocalDate pour la cohérence
     */
    long countCreneauxDisponibles(Long idMedecin, LocalDate date);
}