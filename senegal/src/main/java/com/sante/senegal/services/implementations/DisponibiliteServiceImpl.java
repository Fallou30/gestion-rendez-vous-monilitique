package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;
import com.sante.senegal.repositories.*;
import com.sante.senegal.mappers.DisponibiliteMapper;
import com.sante.senegal.config.DateNagerCalendrierService;
import com.sante.senegal.services.interfaces.DisponibiliteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DisponibiliteServiceImpl implements DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final MedecinRepository medecinRepository;
    private final ServiceRepository serviceRepository;
    private final HopitalRepository hopitalRepository;
    private final DateNagerCalendrierService calendrierService;
    private final DisponibiliteMapper disponibiliteMapper;

    @Override
    public List<DisponibiliteResponseDto> getAllDisponibilites() {
        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    @Override
    public Optional<DisponibiliteResponseDto> getDisponibiliteById(Long id) {
        return disponibiliteRepository.findById(id)
                .map(disponibiliteMapper::toResponseDto);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByMedecin(Long idMedecin) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByMedecinId(idMedecin);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByService(Long idService) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByServiceIdService(idService);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByHopital(Long idHopital) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByHopitalIdHopital(idHopital);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByDate(LocalDate date) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByDate(date);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByStatut(Disponibilite.StatutDisponibilite statut) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByStatut(statut);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getByServiceDateAndStatut(Long idService, LocalDate date,
                                                                    Disponibilite.StatutDisponibilite statut) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByServiceDateAndStatut(idService, date, statut);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getAvailableDisponibilitesBetweenDates(LocalDate dateDebut, LocalDate dateFin) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findAvailableDisponibilitesBetweenDates(dateDebut, dateFin);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public boolean isConflictingDisponibilite(Long idMedecin, LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        List<Disponibilite> conflicts = disponibiliteRepository.findConflictingDisponibilites(idMedecin, date, heureDebut, heureFin);
        return !conflicts.isEmpty();
    }

    /**
     * Vérifie si un médecin est disponible à une date et heure donnée
     */
    public boolean estDisponible(Long medecinId, LocalDateTime dateHeure) {
        LocalDate date = dateHeure.toLocalDate();
        LocalTime heure = dateHeure.toLocalTime();

        // Vérifier si c'est un jour férié
        if (calendrierService.estJourFerie(date)) {
            log.debug("Date {} est un jour férié", date);
            return false;
        }

        // Vérifier les disponibilités normales
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByIdMedecinAndDateAndStatut(medecinId, date, Disponibilite.StatutDisponibilite.DISPONIBLE);

        return disponibilites.stream()
                .anyMatch(dispo ->
                        !heure.isBefore(dispo.getHeureDebut()) &&
                                !heure.isAfter(dispo.getHeureFin())
                );
    }

    /**
     * Génère les disponibilités pour un médecin en excluant les jours fériés
     */
    public void genererDisponibilites(Long medecinId, Long serviceId, Long hopitalId,
                                      LocalDate dateDebut, LocalDate dateFin, List<CreneauHoraire> creneaux) {

        // Valider l'existence des entités
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + medecinId));

        com.sante.senegal.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'ID: " + serviceId));

        Hopital hopital = hopitalRepository.findById(hopitalId)
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + hopitalId));

        List<JourFerie> joursFeries = calendrierService.getJoursFeries(dateDebut, dateFin);
        Set<LocalDate> datesJoursFeries = joursFeries.stream()
                .map(JourFerie::getDate)
                .collect(Collectors.toSet());

        LocalDate date = dateDebut;
        while (!date.isAfter(dateFin)) {
            // Ignorer les jours fériés
            if (!datesJoursFeries.contains(date)) {
                // Ignorer les weekends (optionnel selon les besoins)
                if (!estWeekend(date)) {
                    creerDisponibilitesJour(medecin, service, hopital, date, creneaux);
                }
            }
            date = date.plusDays(1);
        }
    }

    private boolean estWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private void creerDisponibilitesJour(Medecin medecin, com.sante.senegal.entities.Service service,
                                         Hopital hopital, LocalDate date, List<CreneauHoraire> creneaux) {
        for (CreneauHoraire creneau : creneaux) {
            Disponibilite disponibilite = Disponibilite.builder()
                    .medecin(medecin)
                    .service(service)
                    .hopital(hopital)
                    .date(date)
                    .jourSemaine(date.getDayOfWeek())
                    .heureDebut(creneau.getHeureDebut())
                    .heureFin(creneau.getHeureFin())
                    .statut(Disponibilite.StatutDisponibilite.DISPONIBLE)
                    .recurrence(Disponibilite.Recurrence.PONCTUELLE)
                    .build();

            disponibiliteRepository.save(disponibilite);
        }
    }

    /**
     * Récupère les créneaux disponibles en tenant compte des jours fériés
     */
    public List<CreneauLibre> getCreneauxLibres(Long medecinId, LocalDate date) {
        // Vérifier si c'est un jour férié
        if (calendrierService.estJourFerie(date)) {
            return Collections.emptyList();
        }

        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByIdMedecinAndDateAndStatut(medecinId, date, Disponibilite.StatutDisponibilite.DISPONIBLE);

        return disponibilites.stream()
                .map(dispo -> CreneauLibre.builder()
                        .heureDebut(dispo.getHeureDebut())
                        .heureFin(dispo.getHeureFin())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Marque les disponibilités comme indisponibles pour les jours fériés
     */
    @Scheduled(cron = "0 30 1 * * *") // Tous les jours à 1h30
    public void marquerIndisponibiliteJoursFeries() {
        LocalDate demain = LocalDate.now().plusDays(1);
        LocalDate finPeriode = demain.plusMonths(3);

        List<JourFerie> joursFeries = calendrierService.getJoursFeries(demain, finPeriode);

        for (JourFerie jourFerie : joursFeries) {
            List<Disponibilite> disponibilites = disponibiliteRepository
                    .findByDateAndStatut(jourFerie.getDate(), Disponibilite.StatutDisponibilite.DISPONIBLE);

            for (Disponibilite dispo : disponibilites) {
                dispo.setStatut(Disponibilite.StatutDisponibilite.INDISPONIBLE);
                dispo.setMotifIndisponibilite("Jour férié: " + jourFerie.getNom());
                disponibiliteRepository.save(dispo);
            }
        }
    }

    public DisponibiliteResponseDto createDisponibilite(DisponibiliteRequestDto requestDto) {
        // Validation des entités liées
        Medecin medecin = medecinRepository.findById(requestDto.getIdMedecin())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + requestDto.getIdMedecin()));

        com.sante.senegal.entities.Service service = serviceRepository.findById(requestDto.getIdService())
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'ID: " + requestDto.getIdService()));

        Hopital hopital = hopitalRepository.findById(requestDto.getIdHopital())
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + requestDto.getIdHopital()));

        // Vérifier le statut du service
        if (service.getStatut() != com.sante.senegal.entities.Service.StatutService.ACTIF) {
            throw new RuntimeException("Le service est indisponible : " + service.getStatut());
        }

        // Vérifier le statut de l'hôpital
        if (hopital.getStatut() != Hopital.StatutHopital.ACTIF) {
            throw new RuntimeException("L'hôpital est indisponible : " + hopital.getStatut());
        }

        // Vérifier les conflits
        if (isConflictingDisponibilite(requestDto.getIdMedecin(),
                requestDto.getDate(),
                requestDto.getHeureDebut(),
                requestDto.getHeureFin())) {
            throw new RuntimeException("Conflit de disponibilité détecté");
        }

        // Convertir le DTO en entité
        Disponibilite disponibilite = disponibiliteMapper.toEntity(requestDto, medecin, service, hopital);

        // Définir automatiquement le jour de la semaine
        disponibilite.setJourSemaine(requestDto.getDate().getDayOfWeek());

        log.info("Création d'une nouvelle disponibilité pour le médecin {}", requestDto.getIdMedecin());
        Disponibilite savedDisponibilite = disponibiliteRepository.save(disponibilite);

        return disponibiliteMapper.toResponseDto(savedDisponibilite);
    }

    public DisponibiliteResponseDto updateDisponibilite(Long id, DisponibiliteRequestDto requestDto) {
        Disponibilite existingDisponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée avec l'ID: " + id));

        // Validation des entités liées
        Medecin medecin = medecinRepository.findById(requestDto.getIdMedecin())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + requestDto.getIdMedecin()));

        com.sante.senegal.entities.Service service = serviceRepository.findById(requestDto.getIdService())
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec l'ID: " + requestDto.getIdService()));

        Hopital hopital = hopitalRepository.findById(requestDto.getIdHopital())
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + requestDto.getIdHopital()));

        // Mettre à jour l'entité
        disponibiliteMapper.updateEntity(existingDisponibilite, requestDto, medecin, service, hopital);
        existingDisponibilite.setJourSemaine(requestDto.getDate().getDayOfWeek());

        log.info("Mise à jour de la disponibilité avec ID: {}", id);
        Disponibilite updatedDisponibilite = disponibiliteRepository.save(existingDisponibilite);

        return disponibiliteMapper.toResponseDto(updatedDisponibilite);
    }

    public void deleteDisponibilite(Long id) {
        if (disponibiliteRepository.existsById(id)) {
            log.info("Suppression de la disponibilité avec ID: {}", id);
            disponibiliteRepository.deleteById(id);
        } else {
            throw new RuntimeException("Disponibilité non trouvée avec l'ID: " + id);
        }
    }

    public DisponibiliteResponseDto changeStatutDisponibilite(Long id, Disponibilite.StatutDisponibilite nouveauStatut) {
        Disponibilite disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée avec l'ID: " + id));

        disponibilite.setStatut(nouveauStatut);
        log.info("Changement de statut de la disponibilité {} vers {}", id, nouveauStatut);
        Disponibilite updatedDisponibilite = disponibiliteRepository.save(disponibilite);

        return disponibiliteMapper.toResponseDto(updatedDisponibilite);
    }

    public List<DisponibiliteResponseDto> getByMedecinDateAndStatut(Long idMedecin, LocalDate date,
                                                                    Disponibilite.StatutDisponibilite statut) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByMedecinDateAndStatut(idMedecin, date, statut);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByServiceBetweenDates(Long idService, LocalDate dateDebut,
                                                                                 LocalDate dateFin) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByServiceBetweenDates(idService, dateDebut, dateFin);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    public List<DisponibiliteResponseDto> getDisponibilitesByHopitalBetweenDates(Long idHopital, LocalDate dateDebut,
                                                                                 LocalDate dateFin) {
        List<Disponibilite> disponibilites = disponibiliteRepository.findByHopitalBetweenDates(idHopital, dateDebut, dateFin);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    @Transactional
    public void marquerAbsence(Long medecinId, LocalDate dateDebut, LocalDate dateFin, String motif) {
        // Validation des paramètres
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin ne peut pas être null");
        }
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates ne peuvent pas être null");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        if (motif == null || motif.isBlank()) {
            throw new IllegalArgumentException("Le motif ne peut pas être vide");
        }

        // Récupération et mise à jour en une seule opération
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinBetweenDatesAndStatut(
                        medecinId,
                        dateDebut,
                        dateFin,
                        Disponibilite.StatutDisponibilite.DISPONIBLE
                );

        if (disponibilites.isEmpty()) {
            log.warn("Aucune disponibilité trouvée pour le médecin {} entre {} et {}",
                    medecinId, dateDebut, dateFin);
            return;
        }

        disponibilites.forEach(dispo -> {
            dispo.setStatut(Disponibilite.StatutDisponibilite.INDISPONIBLE);
            dispo.setMotifIndisponibilite(motif);
        });

        // Sauvegarde en batch
        disponibiliteRepository.saveAll(disponibilites);

        log.info("{} disponibilités marquées comme indisponibles pour le médecin {} ({} à {}), motif: {}",
                disponibilites.size(), medecinId, dateDebut, dateFin, motif);
    }

    public boolean estMedecinDisponible(Long idMedecin, LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        return disponibiliteRepository.verifierDisponibilite(idMedecin, date, heureDebut, heureFin).isPresent();
    }

    /**
     * Recherche les disponibilités avec des critères multiples
     */
    public List<DisponibiliteResponseDto> rechercherDisponibilites(Long idMedecin, Long idService, Long idHopital,
                                                                   LocalDate dateDebut, LocalDate dateFin,
                                                                   Disponibilite.StatutDisponibilite statut) {
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMultipleCriteria(idMedecin, idService, idHopital, dateDebut, dateFin, statut);
        return disponibiliteMapper.toResponseDtoList(disponibilites);
    }

    /**
     * Obtient le planning complet d'un médecin pour une période donnée
     */
    public Map<LocalDate, List<DisponibiliteResponseDto>> getPlanningMedecin(Long idMedecin,
                                                                             LocalDate dateDebut,
                                                                             LocalDate dateFin) {
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinBetweenDates(idMedecin, dateDebut, dateFin);

        return disponibilites.stream()
                .collect(Collectors.groupingBy(
                        Disponibilite::getDate,
                        Collectors.mapping(
                                disponibiliteMapper::toResponseDto,
                                Collectors.toList()
                        )
                ));
    }

    /**
     * Obtient les statistiques des disponibilités
     */
    public Map<String, Object> getStatistiquesDisponibilites(Long idMedecin, LocalDate dateDebut, LocalDate dateFin) {
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinBetweenDates(idMedecin, dateDebut, dateFin);

        Map<String, Object> statistiques = new HashMap<>();

        long totalDisponibilites = disponibilites.size();
        long disponibilitesLibres = disponibilites.stream()
                .filter(d -> d.getStatut() == Disponibilite.StatutDisponibilite.DISPONIBLE)
                .count();
        long disponibilitesOccupees = disponibilites.stream()
                .filter(d -> d.getStatut() == Disponibilite.StatutDisponibilite.OCCUPE)
                .count();
        long disponibilitesIndisponibles = disponibilites.stream()
                .filter(d -> d.getStatut() == Disponibilite.StatutDisponibilite.INDISPONIBLE)
                .count();

        statistiques.put("totalDisponibilites", totalDisponibilites);
        statistiques.put("disponibilitesLibres", disponibilitesLibres);
        statistiques.put("disponibilitesOccupees", disponibilitesOccupees);
        statistiques.put("disponibilitesIndisponibles", disponibilitesIndisponibles);

        if (totalDisponibilites > 0) {
            statistiques.put("tauxOccupation", (double) disponibilitesOccupees / totalDisponibilites * 100);
            statistiques.put("tauxDisponibilite", (double) disponibilitesLibres / totalDisponibilites * 100);
        }

        return statistiques;
    }

    /**
     * Vérifie la cohérence des disponibilités d'un médecin
     */
    public List<String> verifierCoherenceDisponibilites(Long idMedecin, LocalDate dateDebut, LocalDate dateFin) {
        List<String> problemes = new ArrayList<>();
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinBetweenDates(idMedecin, dateDebut, dateFin);

        // Grouper par date
        Map<LocalDate, List<Disponibilite>> parDate = disponibilites.stream()
                .collect(Collectors.groupingBy(Disponibilite::getDate));

        for (Map.Entry<LocalDate, List<Disponibilite>> entry : parDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Disponibilite> disposDuJour = entry.getValue();

            // Vérifier les chevauchements
            for (int i = 0; i < disposDuJour.size(); i++) {
                for (int j = i + 1; j < disposDuJour.size(); j++) {
                    Disponibilite d1 = disposDuJour.get(i);
                    Disponibilite d2 = disposDuJour.get(j);

                    if (seChevauchent(d1, d2)) {
                        problemes.add(String.format("Chevauchement détecté le %s entre %s-%s et %s-%s",
                                date, d1.getHeureDebut(), d1.getHeureFin(),
                                d2.getHeureDebut(), d2.getHeureFin()));
                    }
                }
            }

            // Vérifier les horaires cohérents
            for (Disponibilite dispo : disposDuJour) {
                if (dispo.getHeureDebut().isAfter(dispo.getHeureFin())) {
                    problemes.add(String.format("Horaire incohérent le %s: début %s après fin %s",
                            date, dispo.getHeureDebut(), dispo.getHeureFin()));
                }
            }
        }

        return problemes;
    }

    private boolean seChevauchent(Disponibilite d1, Disponibilite d2) {
        return !(d1.getHeureFin().isBefore(d2.getHeureDebut()) ||
                d2.getHeureFin().isBefore(d1.getHeureDebut()));
    }
}