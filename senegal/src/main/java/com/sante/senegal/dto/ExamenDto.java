package com.sante.senegal.dto;

import com.sante.senegal.entities.Examen;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExamenDto {
    private Long idExamen;
    private Long consultationId;
    private String typeExamen;
    private String nomExamen;
    private String description;
    private LocalDate datePrescription;
    private LocalDate dateRealisation;
    private String resultats;
    private String interpretation;
    private Examen.StatutExamen statut;
    private Examen.NiveauUrgence urgence;
}
