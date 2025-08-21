package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.RendezVousDto;
import com.sante.senegal.dto.RendezVousRequestDto;
import com.sante.senegal.entities.RendezVous;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousService {
    List<RendezVousDto> getAllRendezVous();
    RendezVousDto getRendezVousById(Long id);
    List<RendezVousDto> getRendezVousByPatient(Long idPatient);
    List<RendezVousDto> getRendezVousByMedecin(Long idMedecin);
    List<RendezVousDto> getRendezVousByService(Long idService);
    List<RendezVousDto> getRendezVousByHopital(Long idHopital);
    List<RendezVousDto> getRendezVousByStatut(RendezVous.StatutRendezVous statut);
    List<RendezVousDto> getRendezVousByNiveauUrgence(RendezVous.NiveauUrgence niveauUrgence);
    List<RendezVousDto> getRendezVousBetweenDates(LocalDateTime debut, LocalDateTime fin);
    List<RendezVousDto> getUpcomingRendezVousByPatient(Long idPatient);

     List<RendezVousDto> getRendezVousProgrammesPourMedecin(Long idMedecin);


    List<RendezVousDto> getOverdueRendezVous();
    Long countRendezVousByMedecinAndDate(Long idMedecin, LocalDateTime debut, LocalDateTime fin);
    RendezVousDto createRendezVous(RendezVousRequestDto dto);
    RendezVousDto updateDateHeureRendezVous(Long id, LocalDateTime nouvelleDate, Integer nouvelleDuree);
    RendezVousDto updateRendezVous(Long id, RendezVousRequestDto dto);
    void deleteRendezVous(Long id);
    RendezVousDto changeStatutRendezVous(Long id, RendezVous.StatutRendezVous statut);
    RendezVousDto confirmerRendezVous(Long id);
    RendezVousDto annulerRendezVous(Long id);
    RendezVousDto reporterRendezVous(Long id, LocalDateTime nouvelleDate);
    RendezVousDto commencerConsultation(Long id);
    RendezVousDto terminerConsultation(Long id);
    List<RendezVousDto> getRendezVousUrgents();
    List<RendezVousDto> getRendezVousDuJour(LocalDateTime date);
    void envoyerRappelsRendezVous(); // Programmé
    List<RendezVousDto> getRendezVousByMedecinAndDateRange(Long idMedecin,LocalDateTime dateDebut,LocalDateTime dateFin);
    List<RendezVousDto> getUpcomingRendezVousByMedecin(Long idMedecin);

}
