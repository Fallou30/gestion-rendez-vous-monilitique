package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Disponibilite;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DisponibiliteService {

    // Méthodes CRUD de base
    List<DisponibiliteResponseDto> getAllDisponibilites();
    Optional<DisponibiliteResponseDto> getDisponibiliteById(Long id);
    DisponibiliteResponseDto createDisponibilite(DisponibiliteRequestDto requestDto);
    DisponibiliteResponseDto updateDisponibilite(Long id, DisponibiliteRequestDto requestDto);
    void deleteDisponibilite(Long id);

    // Méthodes de recherche
    List<DisponibiliteResponseDto> getDisponibilitesByMedecin(Long idMedecin);
    List<DisponibiliteResponseDto> getDisponibilitesByService(Long idService);
    List<DisponibiliteResponseDto> getDisponibilitesByHopital(Long idHopital);
    List<DisponibiliteResponseDto> getDisponibilitesByDate(LocalDate date);
    List<DisponibiliteResponseDto> getDisponibilitesByStatut(Disponibilite.StatutDisponibilite statut);

    // Méthodes de recherche avancée
    List<DisponibiliteResponseDto> getByServiceDateAndStatut(Long idService, LocalDate date,
                                                             Disponibilite.StatutDisponibilite statut);
    List<DisponibiliteResponseDto> getByMedecinDateAndStatut(Long idMedecin, LocalDate date,
                                                             Disponibilite.StatutDisponibilite statut);
    List<DisponibiliteResponseDto> getAvailableDisponibilitesBetweenDates(LocalDate dateDebut, LocalDate dateFin);
    List<DisponibiliteResponseDto> getDisponibilitesByServiceBetweenDates(Long idService, LocalDate dateDebut,
                                                                          LocalDate dateFin);
    List<DisponibiliteResponseDto> getDisponibilitesByHopitalBetweenDates(Long idHopital, LocalDate dateDebut,
                                                                          LocalDate dateFin);

    // Méthodes de recherche avec critères multiples
    List<DisponibiliteResponseDto> rechercherDisponibilites(Long idMedecin, Long idService, Long idHopital,
                                                            LocalDate dateDebut, LocalDate dateFin,
                                                            Disponibilite.StatutDisponibilite statut);

    // Méthodes de vérification
    boolean isConflictingDisponibilite(Long idMedecin, LocalDate date, LocalTime heureDebut, LocalTime heureFin);
    boolean estDisponible(Long medecinId, LocalDateTime dateHeure);
    boolean estMedecinDisponible(Long idMedecin, LocalDate date, LocalTime heureDebut, LocalTime heureFin);

    // Méthodes de gestion du statut
    DisponibiliteResponseDto changeStatutDisponibilite(Long id, Disponibilite.StatutDisponibilite nouveauStatut);
    void marquerAbsence(Long medecinId, LocalDate dateDebut, LocalDate dateFin, String motif);

    // Méthodes de génération et planification
    void genererDisponibilites(Long medecinId, Long serviceId, Long hopitalId,
                               LocalDate dateDebut, LocalDate dateFin, List<CreneauHoraire> creneaux);
    List<CreneauLibre> getCreneauxLibres(Long medecinId, LocalDate date);

    // Méthodes de planning et statistiques
    Map<LocalDate, List<DisponibiliteResponseDto>> getPlanningMedecin(Long idMedecin,
                                                                      LocalDate dateDebut,
                                                                      LocalDate dateFin);
    Map<String, Object> getStatistiquesDisponibilites(Long idMedecin, LocalDate dateDebut, LocalDate dateFin);

    // Méthodes de vérification et contrôle qualité
    List<String> verifierCoherenceDisponibilites(Long idMedecin, LocalDate dateDebut, LocalDate dateFin);

    // Méthodes automatisées (Scheduled)
    void marquerIndisponibiliteJoursFeries();
}