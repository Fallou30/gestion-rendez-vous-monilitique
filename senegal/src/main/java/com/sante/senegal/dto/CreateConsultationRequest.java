package com.sante.senegal.dto;

import lombok.Data;

@Data
public class CreateConsultationRequest {
    private Long rendezVousId;
    private String symptomes;
    private String diagnostic;
    private String observations;
    private String recommandations;
}
