package com.sante.senegal.dto;

import lombok.Data;

@Data
public class CreatePrescriptionRequest {
    private Long consultationId;
    private Integer dureeTraitement;
    private String instructionsGenerales;
}
