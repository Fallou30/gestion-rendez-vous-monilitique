package com.sante.senegal.dto;

import lombok.Data;

@Data
public class UpdateDossierRequest {
    private String antecedentsMedicaux;
    private String antecedentsFamiliaux;
    private String vaccinations;
    private String notesGenerales;
}
