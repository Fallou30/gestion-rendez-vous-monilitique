package com.sante.senegal.dto;

import com.sante.senegal.entities.MedicamentPrescrit;
import lombok.Data;

@Data
public class MedicamentPrescritDto {
    private Long idMedicamentPrescrit;
    private String nomMedicament;
    private String dosage;
    private String frequence;
    private String duree;
    private MedicamentPrescrit.StatutMedicament statutMedicament;
}
