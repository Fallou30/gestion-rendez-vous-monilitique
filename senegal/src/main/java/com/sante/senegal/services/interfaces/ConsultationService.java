package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.ConsultationDto;
import com.sante.senegal.entities.Consultation;
import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {
    ConsultationDto creerConsultation(Long rendezVousId, String symptomes, String diagnostic,
                                   String observations, String recommandations);
    ConsultationDto terminerConsultation(Long consultationId, Integer dureeReelle);
    ConsultationDto mettreAJourConsultation(Long consultationId, ConsultationDto dto);
    List<ConsultationDto> getHistoriqueConsultations(Long patientId);
    List<ConsultationDto> getConsultationsMedecin(Long medecinId, LocalDate dateDebut, LocalDate dateFin);
    ConsultationDto getConsultationById(Long id);
    List<ConsultationDto> getConsultationsPatient(Long patientId, LocalDate dateDebut, LocalDate dateFin);

}