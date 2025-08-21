package com.sante.senegal.dto;

import lombok.Data;

@Data
public class MedicamentPrescritStat {
    private String nomMedicament;
    private Long nombre;

    public MedicamentPrescritStat(String nomMedicament, Long nombre) {
        this.nomMedicament = nomMedicament;
        this.nombre = nombre;
    }

    // Getters and setters
}