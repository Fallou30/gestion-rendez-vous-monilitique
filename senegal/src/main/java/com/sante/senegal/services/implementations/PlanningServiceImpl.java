package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.PlanningDto;
import com.sante.senegal.dto.PlanningReservationRequestDto;
import com.sante.senegal.entities.*;
import com.sante.senegal.repositories.*;
import com.sante.senegal.config.DateNagerCalendrierService;
import com.sante.senegal.services.interfaces.PlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanningServiceImpl implements PlanningService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final PlanningRepository planningRepository;
    private final DateNagerCalendrierService calendrierService;
    private final HopitalRepository hopitalRepository;
    private final AbsenceMedecinRepository absenceMedecinRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ModelMapper modelMapper;

    private static final int DUREE_CRENEAU_MINUTES = 30;
    private static final Map<RendezVous.TypeConsultation, Integer> DUREES_CONSULTATION = Map.of(
            RendezVous.TypeConsultation.CONSULTATION_GENERALE, 30,
            RendezVous.TypeConsultation.CONSULTATION_SPECIALISTE, 45,
            RendezVous.TypeConsultation.CONSULTATION_URGENCE, 20,
            RendezVous.TypeConsultation.CONSULTATION_SUIVI, 25,
            RendezVous.TypeConsultation.CONSULTATION_PREMIERE, 30
    );

    @Override
    public void genererPlanningsPourUnMois(Long idMedecin, Long idHopital) {
        Medecin medecin = medecinRepository.findById(idMedecin)
                .orElseThrow(() -> new RuntimeException("Medecin non trouvé"));

        Hopital hopital = hopitalRepository.findById(idHopital)
                .orElseThrow(() -> new RuntimeException("Hopital non trouvé"));

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);

        List<Disponibilite> disponibilites = disponibiliteRepository.findByMedecinId(idMedecin);
        Map<LocalDate, List<Disponibilite>> dispoParJour = disponibilites.stream()
                .collect(Collectors.groupingBy(Disponibilite::getDate));

        Set<LocalDate> joursFeries = calendrierService
                .getJoursFeries(today, endDate)
                .stream().map(JourFerie::getDate).collect(Collectors.toSet());

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (joursFeries.contains(date) || estWeekend(date) || estMedecinAbsent(medecin, date)) {
                continue;
            }

            List<Disponibilite> disposDuJour = dispoParJour.getOrDefault(date, Collections.emptyList());

            for (Disponibilite dispo : disposDuJour) {
                LocalTime heureDebut = dispo.getHeureDebut();
                LocalTime heureFin = dispo.getHeureFin();

                while (!heureDebut.plusMinutes(DUREE_CRENEAU_MINUTES).isAfter(heureFin)) {
                    LocalTime finCreneau = heureDebut.plusMinutes(DUREE_CRENEAU_MINUTES);

                    boolean existe = planningRepository.existsByMedecinAndDateAndHeureDebut(
                            medecin, date, heureDebut
                    );

                    if (!existe) {
                        Planning planning = Planning.builder()
                                .medecin(dispo.getMedecin())
                                .service(dispo.getService())
                                .hopital(hopital)
                                .date(date)
                                .heureDebut(heureDebut)
                                .heureFin(finCreneau)
                                .reserve(false)
                                .build();

                        planningRepository.save(planning);
                        log.debug("Créneau ajouté le {} de {} à {}", date, heureDebut, finCreneau);
                    }

                    heureDebut = finCreneau;
                }
            }
        }

        log.info("Créneaux générés pour le médecin {} à l'hôpital {} jusqu'au {}", idMedecin, idHopital, endDate);
    }

    @Override
    public List<Planning> getPlanningsByMedecinAndPeriode(Long idMedecin, LocalDate dateDebut, LocalDate dateFin) {
        return planningRepository.findByMedecinIdAndDateBetween(idMedecin, dateDebut, dateFin);
    }

    @Override
    public List<Planning> getCreneauxDisponibles(Long idMedecin, LocalDate date) {
        return planningRepository.findByMedecinIdAndDateAndReserve(idMedecin, date, false);
    }

    @Override
    public List<PlanningDto> getCreneauxDisponiblesDto(Long idMedecin, Long idService, Long idHopital, LocalDate dateDebut, LocalDate dateFin) {
        List<Planning> plannings = planningRepository.findCreneauxDisponibles(idMedecin, idService, idHopital, dateDebut, dateFin);

        return plannings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningDto> getCreneauxDisponiblesParCriteres(Long idMedecin, Long idService, Long idHopital, LocalDate date) {
        List<Planning> plannings = planningRepository.findCreneauxDisponiblesParCriteres(idMedecin, idService, idHopital, date);

        return plannings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlanningDto reserverCreneau(PlanningReservationRequestDto request) {
        // Vérifier que le planning existe et est disponible
        Planning planning = planningRepository.findById(request.getIdPlanning())
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));

        if (planning.isReserve()) {
            throw new RuntimeException("Le créneau est déjà réservé");
        }

        // Récupérer le patient
        Patient patient = patientRepository.findById(request.getIdPatient())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        // Calculer la durée selon le type de consultation
        int duree = DUREES_CONSULTATION.getOrDefault(request.getTypeConsultation(), DUREE_CRENEAU_MINUTES);
        LocalTime nouvelleHeureFin = planning.getHeureDebut().plusMinutes(duree);

        // Créer le rendez-vous
        RendezVous rendezVous = RendezVous.builder()
                .dateHeure(planning.getDate().atTime(planning.getHeureDebut()))
                .dureePrevue(duree)
                .patient(patient)
                .medecin(planning.getMedecin())
                .service(planning.getService())
                .hopital(planning.getHopital())
                .typeConsultation(request.getTypeConsultation())
                .motif(request.getMotif())
                .statut(RendezVous.StatutRendezVous.PROGRAMME)
                .niveauUrgence(RendezVous.NiveauUrgence.NORMALE)
                .dateCreation(LocalDateTime.now())
                .modePriseRdv(RendezVous.ModePriseRdv.EN_LIGNE)
                .build();

        // Sauvegarder le rendez-vous
        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);

        // Mettre à jour le planning
        planning.setRendezVous(savedRendezVous);
        planning.setHeureFin(nouvelleHeureFin);
        planning.setReserve(true);

        Planning savedPlanning = planningRepository.save(planning);

        log.info("Créneau réservé avec succès: Planning ID {}, RDV ID {}, durée {} minutes",
                savedPlanning.getIdPlanning(), savedRendezVous.getIdRdv(), duree);

        return convertToDto(savedPlanning);
    }

    @Override
    @Transactional
    public PlanningDto libererCreneau(Long idPlanning) {
        Planning planning = planningRepository.findById(idPlanning)
                .orElseThrow(() -> new RuntimeException("Planning non trouvé"));

        if (planning.getRendezVous() != null) {
            // Mettre à jour le statut du rendez-vous
            RendezVous rdv = planning.getRendezVous();
            rdv.setStatut(RendezVous.StatutRendezVous.ANNULE);
            rdv.setDateModification(LocalDateTime.now());
            rendezVousRepository.save(rdv);
        }

        // Libérer le créneau
        planning.setRendezVous(null);
        planning.setReserve(false);
        planning.setHeureFin(planning.getHeureDebut().plusMinutes(DUREE_CRENEAU_MINUTES));

        Planning savedPlanning = planningRepository.save(planning);

        log.info("Créneau libéré: Planning ID {}", idPlanning);
        return convertToDto(savedPlanning);
    }

    @Override
    public List<PlanningDto> getCreneauxDisponiblesParMedecin(Long idMedecin, LocalDate dateDebut, LocalDate dateFin) {
        List<Planning> plannings = planningRepository.findByMedecinIdAndDateBetweenAndReserve(idMedecin, dateDebut, dateFin, false);

        return plannings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningDto> getCreneauxDisponiblesParService(Long idService, LocalDate dateDebut, LocalDate dateFin) {
        List<Planning> plannings = planningRepository.findByServiceIdServiceAndDateBetweenAndReserve(idService, dateDebut, dateFin, false);

        return plannings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningDto> getCreneauxDisponiblesParHopital(Long idHopital, LocalDate dateDebut, LocalDate dateFin) {
        List<Planning> plannings = planningRepository.findByHopitalIdHopitalAndDateBetweenAndReserve(idHopital, dateDebut, dateFin, false);

        return plannings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCreneauDisponible(Long idPlanning) {
        return planningRepository.findById(idPlanning)
                .map(planning -> !planning.isReserve())
                .orElse(false);
    }

    @Override
    public long countCreneauxDisponibles(Long idMedecin, LocalDate date) {
        return planningRepository.countByMedecinIdAndDateAndReserve(idMedecin, date, false);
    }

    private PlanningDto convertToDto(Planning planning) {
        PlanningDto dto = modelMapper.map(planning, PlanningDto.class);

        // Enrichir avec les informations du médecin
        if (planning.getMedecin() != null) {
            dto.setIdMedecin(planning.getMedecin().getId());
            dto.setNomMedecin(planning.getMedecin().getNom() + " " + planning.getMedecin().getPrenom());
            dto.setSpecialiteMedecin(planning.getMedecin().getSpecialite());
        }

        // Enrichir avec les informations du service
        if (planning.getService() != null) {
            dto.setIdService(planning.getService().getIdService());
            dto.setNomService(planning.getService().getNom());
        }

        // Enrichir avec les informations de l'hôpital
        if (planning.getHopital() != null) {
            dto.setIdHopital(planning.getHopital().getIdHopital());
            dto.setNomHopital(planning.getHopital().getNom());
            dto.setAdresseHopital(planning.getHopital().getAdresse());
        }

        // Ajouter l'ID du rendez-vous si réservé
        if (planning.getRendezVous() != null) {
            dto.setIdRendezVous(planning.getRendezVous().getIdRdv());
        }

        return dto;
    }

    private boolean estMedecinAbsent(Medecin medecin, LocalDate date) {
        return absenceMedecinRepository.existsByMedecinAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                medecin, date, date
        );
    }

    private boolean estWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}