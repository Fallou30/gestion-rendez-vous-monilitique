package com.sante.senegal.dto;

import lombok.Data;

@Data
public class CreateDossierRequest {
    private Long patientId;
    private String antecedentsMedicaux;
    private String antecedentsFamiliaux;
    private String vaccinations;
}
