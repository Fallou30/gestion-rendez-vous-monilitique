/*
package com.sante.senegal.services;
import com.sante.senegal.dto.CreneauHoraire;
import com.sante.senegal.entities.Disponibilite;
import com.sante.senegal.entities.JourFerie;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.RendezVous;
import com.sante.senegal.entities.RendezVous.TypeConsultation;
import com.sante.senegal.repositories.DisponibiliteRepository;
import com.sante.senegal.repositories.MedecinRepository;
import com.sante.senegal.repositories.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GenerationDisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DateNagerCalendrierService calendrierService;
    private final DisponibiliteService disponibiliteService;

    // Configuration par défaut
    @Value("${medecin.horaires.debut:09:00}")
    private String heureDebutDefaut;

    @Value("${medecin.horaires.fin:13:00}")
    private String heureFinDefaut;

    @Value("${medecin.creneau.duree:30}")
    private Integer dureeCreneauDefaut; // en minutes

    // Durées par type de consultation
    private static final Map<TypeConsultation, Integer> DUREES_CONSULTATION = Map.of(
            TypeConsultation.CONSULTATION_GENERALE, 30,
            TypeConsultation.CONSULTATION_SPECIALISTE, 45,
            TypeConsultation.CONSULTATION_URGENCE, 20,
            TypeConsultation.CONSULTATION_SUIVI, 25,
            TypeConsultation.CONSULTATION_PREMIERE, 30
    );

    */
/**
     * Génère automatiquement les disponibilités pour tous les médecins
     * Exécuté tous les jours à 2h00 pour générer les créneaux des 30 prochains jours
     *//*

    @Scheduled(cron = "0 0 2 * * *")
    public void genererDisponibilitesAutomatique() {
        log.info("Début de la génération automatique des disponibilités");

        LocalDate dateDebut = LocalDate.now().plusDays(1);
        LocalDate dateFin = dateDebut.plusDays(30);

        List<Medecin> medecins = medecinRepository.findAll();

        for (Medecin medecin : medecins) {
            try {
                genererDisponibilitesPourMedecin(medecin.getId(), dateDebut, dateFin);
            } catch (Exception e) {
                log.error("Erreur lors de la génération des disponibilités pour le médecin {}: {}",
                        medecin.getId(), e.getMessage());
            }
        }

        log.info("Fin de la génération automatique des disponibilités");
    }

    */
/**
     * Génère les disponibilités pour un médecin spécifique
     *//*

    public void genererDisponibilitesPourMedecin(Long medecinId, LocalDate dateDebut, LocalDate dateFin) {
        log.info("Génération des disponibilités pour le médecin {} du {} au {}",
                medecinId, dateDebut, dateFin);

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé: " + medecinId));

        // Supprimer les anciennes disponibilités futures non réservées
        supprimerAnciennesDisponibilites(medecinId, dateDebut, dateFin);

        // Obtenir les jours fériés pour la période
        Set<LocalDate> joursFeries = calendrierService.getJoursFeries(dateDebut, dateFin)
                .stream()
                .map(JourFerie::getDate)
                .collect(Collectors.toSet());

        LocalDate date = dateDebut;
        while (!date.isAfter(dateFin)) {
            // Générer seulement pour les jours ouvrables (lundi à vendredi)
            if (estJourOuvrable(date) && !joursFeries.contains(date)) {
                genererCreneauxJour(medecin, date);
            }
            date = date.plusDays(1);
        }
    }

    */
/**
     * Génère les créneaux pour une journée donnée
     *//*

    private void genererCreneauxJour(Medecin medecin, LocalDate date) {
        LocalTime heureDebut = LocalTime.parse(heureDebutDefaut);
        LocalTime heureFin = LocalTime.parse(heureFinDefaut);

        // Récupérer les rendez-vous existants pour cette date
        List<RendezVous> rendezVousExistants = rendezVousRepository
                .findByMedecinAndDateBetween(medecin.getId(),
                        date.atStartOfDay(),
                        date.atTime(23, 59, 59));

        // Créer les créneaux de base
        List<CreneauHoraire> creneauxLibres = creerCreneauxBase(heureDebut, heureFin);

        // Exclure les créneaux déjà réservés
        creneauxLibres = excluereCreneauxReserves(creneauxLibres, rendezVousExistants);

        // Sauvegarder les disponibilités
        for (CreneauHoraire creneau : creneauxLibres) {
            Disponibilite disponibilite = Disponibilite.builder()
                    .medecin(medecin)
                    .date(date)
                    .heureDebut(creneau.getHeureDebut())
                    .heureFin(creneau.getHeureFin())
                    .statut(Disponibilite.StatutDisponibilite.DISPONIBLE)
                    .build();

            disponibiliteService.createDisponibilite(disponibilite);
        }
    }

    */
/**
     * Crée les créneaux de base pour une journée
     *//*

    private List<CreneauHoraire> creerCreneauxBase(LocalTime heureDebut, LocalTime heureFin) {
        List<CreneauHoraire> creneaux = new ArrayList<>();
        LocalTime heureActuelle = heureDebut;

        while (heureActuelle.isBefore(heureFin)) {
            LocalTime finCreneau = heureActuelle.plusMinutes(dureeCreneauDefaut);
            if (finCreneau.isAfter(heureFin)) {
                break;
            }

            creneaux.add(CreneauHoraire.builder()
                    .heureDebut(heureActuelle)
                    .heureFin(finCreneau)
                    .build());

            heureActuelle = finCreneau;
        }

        return creneaux;
    }

    */
/**
     * Exclut les créneaux déjà réservés par des rendez-vous
     *//*

    private List<CreneauHoraire> excluereCreneauxReserves(List<CreneauHoraire> creneauxLibres,
                                                          List<RendezVous> rendezVousExistants) {
        return creneauxLibres.stream()
                .filter(creneau -> !estCreneauReserve(creneau, rendezVousExistants))
                .collect(Collectors.toList());
    }

    */
/**
     * Vérifie si un créneau est déjà réservé
     *//*

    private boolean estCreneauReserve(CreneauHoraire creneau, List<RendezVous> rendezVousExistants) {
        return rendezVousExistants.stream()
                .anyMatch(rdv -> {
                    LocalTime heureRdv = rdv.getDateHeure().toLocalTime();
                    LocalTime finRdv = heureRdv.plusMinutes(rdv.getDureePrevue() != null ?
                            rdv.getDureePrevue() : dureeCreneauDefaut);

                    return !(creneau.getHeureFin().isBefore(heureRdv) ||
                            creneau.getHeureDebut().isAfter(finRdv));
                });
    }

    */
/**
     * Supprime les anciennes disponibilités non réservées
     *//*

    private void supprimerAnciennesDisponibilites(Long medecinId, LocalDate dateDebut, LocalDate dateFin) {
        List<Disponibilite> anciennesDisponibilites = disponibiliteRepository
                .findByMedecinIdAndDateBetweenAndStatut(medecinId, dateDebut, dateFin,
                        Disponibilite.StatutDisponibilite.DISPONIBLE);

        disponibiliteRepository.deleteAll(anciennesDisponibilites);
    }

    */
/**
     * Vérifie si c'est un jour ouvrable (lundi à vendredi)
     *//*

    private boolean estJourOuvrable(LocalDate date) {
        DayOfWeek jour = date.getDayOfWeek();
        return jour != DayOfWeek.SATURDAY && jour != DayOfWeek.SUNDAY;
    }

    */
/**
     * Génère les disponibilités pour un médecin avec des horaires personnalisés
     *//*

    public void genererDisponibilitesPersonnalisees(Long medecinId, LocalDate dateDebut,
                                                    LocalDate dateFin, LocalTime heureDebut,
                                                    LocalTime heureFin, Integer dureeMinutes) {
        log.info("Génération personnalisée pour le médecin {} avec horaires {}h à {}h",
                medecinId, heureDebut, heureFin);

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé: " + medecinId));

        LocalDate date = dateDebut;
        while (!date.isAfter(dateFin)) {
            if (estJourOuvrable(date) && !calendrierService.estJourFerie(date)) {
                genererCreneauxJourPersonnalise(medecin, date, heureDebut, heureFin, dureeMinutes);
            }
            date = date.plusDays(1);
        }
    }

    */
/**
     * Génère les créneaux pour une journée avec des paramètres personnalisés
     *//*

    private void genererCreneauxJourPersonnalise(Medecin medecin, LocalDate date,
                                                 LocalTime heureDebut, LocalTime heureFin,
                                                 Integer dureeMinutes) {
        //List<CreneauHoraire> creneaux = new ArrayList<>();
        LocalTime heureActuelle = heureDebut;

        while (heureActuelle.isBefore(heureFin)) {
            LocalTime finCreneau = heureActuelle.plusMinutes(dureeMinutes);
            if (finCreneau.isAfter(heureFin)) {
                break;
            }

            Disponibilite disponibilite = Disponibilite.builder()
                    .medecin(medecin)
                    .date(date)
                    .heureDebut(heureActuelle)
                    .heureFin(finCreneau)
                    .statut(Disponibilite.StatutDisponibilite.DISPONIBLE)
                    .build();

            disponibiliteRepository.save(disponibilite);
            heureActuelle = finCreneau;
        }
    }

    */
/**
     * Calcule la durée d'un rendez-vous selon son type
     *//*

    public Integer calculerDureeRendezVous(TypeConsultation typeConsultation) {
        return DUREES_CONSULTATION.getOrDefault(typeConsultation, dureeCreneauDefaut);
    }

    */
/**
     * Méthode pour régénérer les disponibilités d'un médecin après modification
     *//*

    public void regenererDisponibilitesMedecin(Long medecinId) {
        LocalDate dateDebut = LocalDate.now().plusDays(1);
        LocalDate dateFin = dateDebut.plusDays(30);
        genererDisponibilitesPourMedecin(medecinId, dateDebut, dateFin);
    }

    */
/**
     * Génère les disponibilités pour une période spécifique (utile pour les tests)
     *//*

    public void genererDisponibilitesPeriode(LocalDate dateDebut, LocalDate dateFin) {
        List<Medecin> medecins = medecinRepository.findAll();

        for (Medecin medecin : medecins) {
            genererDisponibilitesPourMedecin(medecin.getId(), dateDebut, dateFin);
        }
    }
}*/
