package com.sante.senegal.dto;

import lombok.Data;

@Data
public class CreateMedicamentRequest {
    private String nomMedicament;
    private String dosage;
    private String frequence;
    private Integer duree;
    private String instructionsSpecifiques;
    private Integer quantitePrescrite;
}
