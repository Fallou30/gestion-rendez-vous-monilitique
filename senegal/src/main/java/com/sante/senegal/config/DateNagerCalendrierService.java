package com.sante.senegal.config;

import com.sante.senegal.dto.DateNagerCountry;
import com.sante.senegal.dto.DateNagerCountryInfo;
import com.sante.senegal.dto.DateNagerHoliday;
import com.sante.senegal.entities.JourFerie;
import com.sante.senegal.repositories.JourFerieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DateNagerCalendrierService {

    private final JourFerieRepository jourFerieRepository;

    private final RestTemplate restTemplate;

    private static final String DATE_NAGER_BASE_URL = "https://date.nager.at/api/v3";
    private static final String COUNTRY_CODE = "SN"; // Sénégal

    public DateNagerCalendrierService(JourFerieRepository jourFerieRepository, RestTemplate restTemplate) {
        this.jourFerieRepository = jourFerieRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Synchronise les jours fériés pour une année donnée
     */
    @Scheduled(cron = "0 0 2 1 1 *") // Tous les 1er janvier à 2h
    public void synchroniserJoursFeries() {
        int anneeActuelle = LocalDate.now().getYear();
        synchroniserJoursFeries(anneeActuelle);
        synchroniserJoursFeries(anneeActuelle + 1);
    }

    public void synchroniserJoursFeries(int annee) {
        try {
            log.info("Synchronisation des jours fériés pour l'année {} avec Date Nager", annee);

            // Nettoyer les anciennes données de cette source pour l'année
            jourFerieRepository.deleteBySourceApiAndAnnee("date-nager", annee);

            String url = String.format("%s/PublicHolidays/%d/%s",
                    DATE_NAGER_BASE_URL, annee, COUNTRY_CODE);

            log.debug("Appel API: {}", url);

            DateNagerHoliday[] holidays = restTemplate.getForObject(url, DateNagerHoliday[].class);

            if (holidays != null && holidays.length > 0) {
                for (DateNagerHoliday holiday : holidays) {
                    sauvegarderJourFerie(holiday);
                }
                log.info("Synchronisation terminée: {} jours fériés ajoutés pour l'année {}",
                        holidays.length, annee);
            } else {
                log.warn("Aucun jour férié trouvé pour l'année {}", annee);
                // Utiliser les données par défaut si l'API ne retourne rien
                utiliserDonneesParDefautSenegal(annee);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la synchronisation des jours fériés pour l'année {}: {}",
                    annee, e.getMessage());
            // Fallback vers les données par défaut
            utiliserDonneesParDefautSenegal(annee);
        }
    }

    private void sauvegarderJourFerie(DateNagerHoliday holiday) {
        try {
            LocalDate date = LocalDate.parse(holiday.getDate());

            // Vérifier si le jour férié existe déjà pour cette date
            Optional<JourFerie> existant = jourFerieRepository
                    .findByDateAndSourceApi(date, "date-nager");

            if (existant.isEmpty()) {
                JourFerie jourFerie = JourFerie.builder()
                        .date(date)
                        .nom(holiday.getName())
                        .description(holiday.getLocalName())
                        .type(determinerTypeJourFerie(holiday))
                        .sourceApi("date-nager")
                        .externalId(holiday.getDate()) // Utiliser la date comme ID externe
                        .affecteDisponibilites(true)
                        .estRecurrent(false)
                        .build();

                jourFerieRepository.save(jourFerie);
                log.debug("Jour férié sauvegardé: {} - {}", date, holiday.getName());
            } else {
                log.debug("Jour férié déjà existant: {} - {}", date, holiday.getName());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du jour férié {}: {}",
                    holiday.getName(), e.getMessage());
        }
    }

    private JourFerie.TypeJourFerie determinerTypeJourFerie(DateNagerHoliday holiday) {
        // Date Nager ne fournit pas de type explicite, donc on utilise des heuristiques
        String nom = holiday.getName().toLowerCase();
        String nomLocal = holiday.getLocalName() != null ? holiday.getLocalName().toLowerCase() : "";

        if (nom.contains("religious") || nom.contains("religieux") ||
                nomLocal.contains("religieux") || nom.contains("islamic") ||
                nom.contains("christian") || nom.contains("catholique")) {
            return JourFerie.TypeJourFerie.RELIGIEUX;
        }

        if (nom.contains("regional") || nom.contains("local") ||
                nomLocal.contains("regional") || nomLocal.contains("local")) {
            return JourFerie.TypeJourFerie.REGIONAL;
        }

        return JourFerie.TypeJourFerie.NATIONAL;
    }

    /**
     * Données par défaut pour le Sénégal si l'API échoue
     */
    private void utiliserDonneesParDefautSenegal(int annee) {
        log.info("Utilisation des données par défaut pour le Sénégal - année {}", annee);

        // Jours fériés fixes du Sénégal
        Map<String, String> joursFeriesFixesSenegal = Map.of(
                "01-01", "Jour de l'An",
                "04-04", "Fête de l'Indépendance",
                "05-01", "Fête du Travail",
                "08-15", "Assomption",
                "11-01", "Toussaint",
                "12-25", "Noël"
        );

        for (Map.Entry<String, String> entry : joursFeriesFixesSenegal.entrySet()) {
            try {
                LocalDate date = LocalDate.parse(annee + "-" + entry.getKey());

                Optional<JourFerie> existant = jourFerieRepository
                        .findByDateAndSourceApi(date, "defaut");

                if (existant.isEmpty()) {
                    JourFerie jourFerie = JourFerie.builder()
                            .date(date)
                            .nom(entry.getValue())
                            .description("Jour férié national du Sénégal")
                            .type(JourFerie.TypeJourFerie.NATIONAL)
                            .sourceApi("defaut")
                            .affecteDisponibilites(true)
                            .estRecurrent(true)
                            .build();

                    jourFerieRepository.save(jourFerie);
                    log.debug("Jour férié par défaut ajouté: {} - {}", date, entry.getValue());
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'ajout du jour férié par défaut {}: {}",
                        entry.getValue(), e.getMessage());
            }
        }
    }

    /**
     * Vérifie si une date est un jour férié
     */
    public boolean estJourFerie(LocalDate date) {
        return jourFerieRepository.existsByDateAndAffecteDisponibilites(date, true);
    }

    /**
     * Récupère les jours fériés pour une période donnée
     */
    public List<JourFerie> getJoursFeries(LocalDate debut, LocalDate fin) {
        return jourFerieRepository.findByDateBetweenAndAffecteDisponibilites(debut, fin, true);
    }

    /**
     * Récupère les jours fériés pour un mois donné
     */
    public List<JourFerie> getJoursFeriesDuMois(int mois, int annee) {
        LocalDate debut = LocalDate.of(annee, mois, 1);
        LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());
        return getJoursFeries(debut, fin);
    }

    /**
     * Synchronisation manuelle (pour les tests ou administration)
     */
    public void synchroniserManuellement(int annee) {
        log.info("Synchronisation manuelle demandée pour l'année {}", annee);
        synchroniserJoursFeries(annee);
    }

    /**
     * Récupère les pays supportés par Date Nager
     */
    public List<String> getPaysSupportes() {
        try {
            String url = DATE_NAGER_BASE_URL + "/AvailableCountries";
            DateNagerCountry[] countries = restTemplate.getForObject(url, DateNagerCountry[].class);

            if (countries != null) {
                return Arrays.stream(countries)
                        .map(country -> country.getCountryCode() + " - " + country.getName())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des pays supportés: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Test de connectivité avec l'API
     */
    public boolean testerConnectivite() {
        try {
            String url = DATE_NAGER_BASE_URL + "/CountryInfo/" + COUNTRY_CODE;
            DateNagerCountryInfo info = restTemplate.getForObject(url, DateNagerCountryInfo.class);
            log.info("Test de connectivité réussi. Pays: {}", info != null ? info.getOfficialName() : "Unknown");
            return true;
        } catch (Exception e) {
            log.error("Test de connectivité échoué: {}", e.getMessage());
            return false;
        }
    }
}
