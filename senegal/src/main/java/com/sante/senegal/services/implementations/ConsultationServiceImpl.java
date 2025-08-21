package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.ConsultationDto;
import com.sante.senegal.entities.*;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.RendezVousRepository;
import com.sante.senegal.services.interfaces.ConsultationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ModelMapper modelMapper;

    @Override
    public ConsultationDto creerConsultation(Long rendezVousId, String symptomes,
                                             String diagnostic, String observations,
                                             String recommandations) {
        RendezVous rdv = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé"));

        validateConsultationPreconditions(rdv);

        Consultation consultation = buildConsultation(rdv, symptomes, diagnostic, observations, recommandations);
        Consultation saved = consultationRepository.save(consultation);
        return convertToDto(saved);
    }

    @Override
    public ConsultationDto terminerConsultation(Long consultationId, Integer dureeReelle) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));

        consultation.setStatut(Consultation.StatutConsultation.TERMINEE);
        consultation.setDureeReelle(dureeReelle);

        if (consultation.getRendezVous() != null) {
            consultation.getRendezVous().setStatut(RendezVous.StatutRendezVous.TERMINE);
        }

        return convertToDto(consultationRepository.save(consultation));
    }

    @Override
    public ConsultationDto mettreAJourConsultation(Long consultationId, ConsultationDto dto) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));

        updateConsultationFromDto(consultation, dto);
        return convertToDto(consultationRepository.save(consultation));
    }

    @Override
    public List<ConsultationDto> getHistoriqueConsultations(Long patientId) {
        return consultationRepository.findByDossierPatientIdOrderByDateHeureDesc(patientId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDto> getConsultationsMedecin(Long medecinId, LocalDate dateDebut, LocalDate dateFin) {
        return consultationRepository.findByRendezVousMedecinIdAndDateHeureBetween(
                        medecinId,
                        dateDebut.atStartOfDay(),
                        dateFin.atTime(23, 59, 59))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationDto> getConsultationsPatient(Long patientId, LocalDate dateDebut, LocalDate dateFin) {
        return consultationRepository.findByDossierPatientIdAndDateHeureBetween(
                        patientId,
                        dateDebut.atStartOfDay(),
                        dateFin.atTime(23, 59, 59))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConsultationDto getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));
        return convertToDto(consultation);
    }

    // Méthodes privées utilitaires
    private void validateConsultationPreconditions(RendezVous rdv) {
        if (rdv.getStatut() != RendezVous.StatutRendezVous.CONFIRME) {
            throw new IllegalStateException("Le rendez-vous doit être confirmé");
        }

        if (rdv.getPatient() == null || rdv.getPatient().getDossierMedical() == null) {
            throw new IllegalStateException("Dossier médical non trouvé");
        }
    }

    private Consultation buildConsultation(RendezVous rdv, String symptomes,
                                           String diagnostic, String observations,
                                           String recommandations) {
        return Consultation.builder()
                .rendezVous(rdv)
                .dossier(rdv.getPatient().getDossierMedical())
                .dateHeure(LocalDateTime.now())
                .symptomes(symptomes)
                .diagnostic(diagnostic)
                .observations(observations)
                .recommandations(recommandations)
                .statut(Consultation.StatutConsultation.EN_COURS)
                .build();
    }

    private void updateConsultationFromDto(Consultation consultation, ConsultationDto dto) {
        if (dto.getSymptomes() != null) {
            consultation.setSymptomes(dto.getSymptomes());
        }
        if (dto.getDiagnostic() != null) {
            consultation.setDiagnostic(dto.getDiagnostic());
        }
        if (dto.getObservations() != null) {
            consultation.setObservations(dto.getObservations());
        }
        if (dto.getRecommandations() != null) {
            consultation.setRecommandations(dto.getRecommandations());
        }
        if (dto.getStatut() != null) {
            consultation.setStatut(dto.getStatut());
        }
        if (dto.getSatisfaction() != null) {
            consultation.setSatisfaction(dto.getSatisfaction());
        }
        if (dto.getDureeReelle() != null) {
            consultation.setDureeReelle(dto.getDureeReelle());
        }
    }

    private ConsultationDto convertToDto(Consultation consultation) {
        ConsultationDto dto = modelMapper.map(consultation, ConsultationDto.class);

        // Gestion manuelle des relations
        if (consultation.getRendezVous() != null) {
            dto.setIdRendezVous(consultation.getRendezVous().getIdRdv());
        }
        if (consultation.getDossier() != null) {
            dto.setIdDossier(consultation.getDossier().getIdDossier());
        }

        return dto;
    }
}