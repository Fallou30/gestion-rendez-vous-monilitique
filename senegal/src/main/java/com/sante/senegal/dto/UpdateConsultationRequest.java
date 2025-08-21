package com.sante.senegal.dto;

import com.sante.senegal.entities.Consultation;
import lombok.Data;

@Data
public class UpdateConsultationRequest {
    private String symptomes;
    private String diagnostic;
    private String observations;
    private String recommandations;
    private Consultation.StatutConsultation statutConsultation;
    private Double satisfaction;

}
