package com.sante.senegal.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningDto {
    private Long idPlanning;
    private Long idMedecin;
    private String nomMedecin;
    private String specialiteMedecin; // Ajouté

    private Long idService;
    private String nomService;

    private Long idHopital;
    private String nomHopital;
    private String adresseHopital; // Ajouté

    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    private boolean reserve;
    private Long idRendezVous; // Peut être null si non réservé
}
