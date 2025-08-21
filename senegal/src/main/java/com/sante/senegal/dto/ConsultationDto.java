package com.sante.senegal.dto;

import com.sante.senegal.entities.Consultation;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationDto {
    private Long idConsultation;
    private Long idRendezVous;
    private Long idDossier;
    private LocalDateTime dateHeure;
    private Integer dureeReelle;
    private String symptomes;
    private String diagnostic;
    private String observations;
    private String recommandations;
    private Consultation.StatutConsultation statut;
    private Double satisfaction;

}