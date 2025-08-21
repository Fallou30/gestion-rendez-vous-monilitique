package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.RendezVousDto;
import com.sante.senegal.dto.RendezVousRequestDto;
import com.sante.senegal.entities.Planning;
import com.sante.senegal.entities.RendezVous;
import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.repositories.PlanningRepository;
import com.sante.senegal.repositories.RendezVousRepository;

import com.sante.senegal.services.interfaces.DisponibiliteService;
import com.sante.senegal.services.interfaces.EmailService;
import com.sante.senegal.services.interfaces.RendezVousService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    private final PlanningRepository planningRepository;
    private final EmailService emailService;
    private final DisponibiliteService disponibiliteService;
    private final ModelMapper modelMapper;

    @Override
    public List<RendezVousDto> getAllRendezVous() {
        return rendezVousRepository.findAll().stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RendezVousDto getRendezVousById(Long id) {
        return modelMapper.map(rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé avec l'ID: " + id)), RendezVousDto.class);
    }

    @Override
    public List<RendezVousDto> getRendezVousByPatient(Long idPatient) {
        return rendezVousRepository.findByPatientId(idPatient).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousByMedecin(Long idMedecin) {
        return rendezVousRepository.findByMedecinId(idMedecin).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousByService(Long idService) {
        return rendezVousRepository.findByServiceIdService(idService).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousByHopital(Long idHopital) {
        return rendezVousRepository.findByHopitalIdHopital(idHopital).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousByStatut(RendezVous.StatutRendezVous statut) {
        return rendezVousRepository.findByStatut(statut).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousByNiveauUrgence(RendezVous.NiveauUrgence niveauUrgence) {
        return rendezVousRepository.findByNiveauUrgence(niveauUrgence).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public RendezVousDto createRendezVous(RendezVousRequestDto dto) {
        RendezVous rdv = modelMapper.map(dto, RendezVous.class);

        // Trouver un créneau disponible dans le planning
        Optional<Planning> creneauDisponible = planningRepository
                .findByMedecinIdAndDateAndHeureDebutAndReserve(
                        dto.getIdMedecin(),
                        dto.getDateHeure().toLocalDate(),
                        dto.getDateHeure().toLocalTime(),
                        false
                )
                .stream()
                .findFirst();

        if (creneauDisponible.isPresent()) {
            Planning creneau = creneauDisponible.get();
            // Sauvegarder d'abord le rendez-vous
            RendezVous savedRdv = rendezVousRepository.save(rdv);
            // Associer le rendez-vous au créneau et réserver
            creneau.setRendezVous(savedRdv);
            creneau.setReserve(true);
            planningRepository.save(creneau);

            return modelMapper.map(savedRdv, RendezVousDto.class);
        }
        throw new IllegalStateException("Aucun créneau disponible trouvé pour ce médecin à cette heure.");
    }

    @Override
    public void deleteRendezVous(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        // Libérer le créneau associé
        planningRepository.findByRendezVousIdRdv(id).ifPresent(creneau -> {
            creneau.setRendezVous(null);
            creneau.setReserve(false);
            planningRepository.save(creneau);
        });

        rendezVousRepository.deleteById(id);
    }

    @Override
    public RendezVousDto annulerRendezVous(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        // Libérer le créneau associé
        planningRepository.findByRendezVousIdRdv(id).ifPresent(creneau -> {
            creneau.setRendezVous(null);
            creneau.setReserve(false);
            planningRepository.save(creneau);
        });

        return changeStatutRendezVous(id, RendezVous.StatutRendezVous.ANNULE);
    }

    @Override
    public RendezVousDto reporterRendezVous(Long id, LocalDateTime nouvelleDate) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        // Libérer l'ancien créneau
        planningRepository.findByRendezVousIdRdv(id).ifPresent(creneau -> {
            creneau.setRendezVous(null);
            creneau.setReserve(false);
            planningRepository.save(creneau);
        });

        // Trouver un nouveau créneau disponible
        Optional<Planning> nouveauCreneau = planningRepository
                .findByMedecinIdAndDateAndHeureDebutAndReserve(
                        rdv.getMedecin().getId(),
                        nouvelleDate.toLocalDate(),
                        nouvelleDate.toLocalTime(),
                        false
                )
                .stream()
                .findFirst();

        if (nouveauCreneau.isPresent()) {
            Planning creneau = nouveauCreneau.get();
            creneau.setRendezVous(rdv);
            creneau.setReserve(true);
            planningRepository.save(creneau);
        } else {
            throw new IllegalStateException("Aucun créneau disponible trouvé pour la nouvelle date");
        }

        rdv.setDateHeure(nouvelleDate);
        rdv.setStatut(RendezVous.StatutRendezVous.REPORTE);
        return modelMapper.map(rendezVousRepository.save(rdv), RendezVousDto.class);
    }

    @Override
    public RendezVousDto terminerConsultation(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));
        if (rdv.getStatut() != RendezVous.StatutRendezVous.EN_COURS)
            throw new IllegalStateException("Le RDV n'est pas en cours.");

        // Libérer le créneau associé
        planningRepository.findByRendezVousIdRdv(id).ifPresent(creneau -> {
            creneau.setRendezVous(null);
            creneau.setReserve(false);
            planningRepository.save(creneau);
        });

        return changeStatutRendezVous(id, RendezVous.StatutRendezVous.TERMINE);
    }

    private boolean isMedecinAvailable(Long idMedecin, LocalDateTime dateHeure) {
        return planningRepository.existsByMedecinIdAndDateAndHeureDebutAndReserve(
                idMedecin,
                dateHeure.toLocalDate(),
                dateHeure.toLocalTime(),
                false
        );
    }

    @Override
    public List<RendezVousDto> getRendezVousBetweenDates(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return rendezVousRepository.findRendezVousBetweenDates(dateDebut, dateFin).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getUpcomingRendezVousByPatient(Long idPatient) {
        return rendezVousRepository.findUpcomingRendezVousByPatient(idPatient).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getOverdueRendezVous() {
        return rendezVousRepository.findOverdueRendezVous(LocalDateTime.now()).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long countRendezVousByMedecinAndDate(Long idMedecin, LocalDateTime debut, LocalDateTime fin) {
        return rendezVousRepository.countRendezVousByMedecinAndDate(idMedecin, debut, fin);
    }


    @Override
    public RendezVousDto updateDateHeureRendezVous(Long id, LocalDateTime nouvelleDate, Integer nouvelleDuree) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        if (rdv.getStatut() == RendezVous.StatutRendezVous.TERMINE ||
                rdv.getStatut() == RendezVous.StatutRendezVous.ANNULE) {
            throw new IllegalStateException("Impossible de modifier ce RDV.");
        }

        if (nouvelleDuree == null) nouvelleDuree = 30;

        boolean dispo = disponibiliteService.estMedecinDisponible(
                rdv.getMedecin().getId(), nouvelleDate.toLocalDate(),
                nouvelleDate.toLocalTime(), nouvelleDate.toLocalTime().plusMinutes(nouvelleDuree));

        if (!dispo) throw new IllegalStateException("Créneau non disponible.");

        rdv.setDateHeure(nouvelleDate);
        rdv.setDureePrevue(nouvelleDuree);
        return modelMapper.map(rendezVousRepository.save(rdv), RendezVousDto.class);
    }

    @Override
    public RendezVousDto updateRendezVous(Long id, RendezVousRequestDto dto) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        modelMapper.map(dto, rdv);
        return modelMapper.map(rendezVousRepository.save(rdv), RendezVousDto.class);
    }

    @Override
    public RendezVousDto changeStatutRendezVous(Long id, RendezVous.StatutRendezVous statut) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));
        rdv.setStatut(statut);
        return modelMapper.map(rendezVousRepository.save(rdv), RendezVousDto.class);
    }
    @Override
    public List<RendezVousDto> getRendezVousProgrammesPourMedecin(Long idMedecin) {
        List<RendezVous> rdvs = rendezVousRepository.findByMedecinIdAndStatut(idMedecin, RendezVous.StatutRendezVous.PROGRAMME);
        return rdvs.stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public RendezVousDto confirmerRendezVous(Long idRdv) {
        RendezVous rdv = rendezVousRepository.findById(idRdv)
                .orElseThrow(() -> new EntityNotFoundException("RDV non trouvé"));

        if (rdv.getStatut() != RendezVous.StatutRendezVous.PROGRAMME) {
            throw new IllegalStateException("Seuls les RDV programmés peuvent être confirmés.");
        }

        rdv.setStatut(RendezVous.StatutRendezVous.CONFIRME);
        return modelMapper.map(rendezVousRepository.save(rdv), RendezVousDto.class);
    }

    @Override
    public RendezVousDto commencerConsultation(Long id) {
        return changeStatutRendezVous(id, RendezVous.StatutRendezVous.EN_COURS);
    }

    @Override
    public List<RendezVousDto> getRendezVousUrgents() {
        return rendezVousRepository.findByNiveauUrgence(RendezVous.NiveauUrgence.TRES_URGENT).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendezVousDto> getRendezVousDuJour(LocalDateTime date) {
        LocalDateTime debut = date.toLocalDate().atStartOfDay();
        LocalDateTime fin = debut.plusDays(1).minusSeconds(1);
        return rendezVousRepository.findRendezVousBetweenDates(debut, fin).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 8 * * *")
    public void envoyerRappelsRendezVous() {
        LocalDateTime maintenant = LocalDateTime.now();
        envoyerRappelsPourJour(maintenant.plusDays(1), 1);
        envoyerRappelsPourJour(maintenant.plusDays(7), 7);
    }

    private void envoyerRappelsPourJour(LocalDateTime jour, int joursAvant) {
        LocalDateTime debut = jour.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = jour.withHour(23).withMinute(59).withSecond(59);

        rendezVousRepository.findRendezVousBetweenDates(debut, fin).forEach(rdv -> {
            if (rdv.getStatut() != RendezVous.StatutRendezVous.PROGRAMME &&
                    rdv.getStatut() != RendezVous.StatutRendezVous.CONFIRME) return;

            Utilisateur patient = rdv.getPatient();
            String date = rdv.getDateHeure().toLocalDate().toString();
            String heure = rdv.getDateHeure().toLocalTime().toString();
            String sujet = "Rappel de votre rendez-vous médical";
            String message = String.format("""
                Bonjour %s %s,
                
                Ceci est un rappel : vous avez un rendez-vous dans %d jour(s).
                📅 Date : %s à %s
                🔎 Motif : %s
                
                Merci de confirmer votre présence.
                
                Santé Sénégal""",
                    patient.getPrenom(), patient.getNom(), joursAvant, date, heure, rdv.getMotif());

            emailService.envoyerNotificationRendezVous(patient.getEmail(), sujet, message);
            log.info("Rappel envoyé à {} pour RDV {}", patient.getEmail(), rdv.getIdRdv());
        });
    }

    @Override
    public List<RendezVousDto> getRendezVousByMedecinAndDateRange(Long idMedecin, LocalDateTime dateDebut, LocalDateTime dateFin) {
        return rendezVousRepository.findByMedecinAndDateRange(idMedecin, dateDebut, dateFin).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());

    }
    @Override
    public List<RendezVousDto> getUpcomingRendezVousByMedecin(Long idMedecin) {
        return rendezVousRepository.findUpcomingRendezVousByMedecin(idMedecin).stream()
                .map(rdv -> modelMapper.map(rdv, RendezVousDto.class))
                .collect(Collectors.toList());
    }


}
