package com.sante.senegal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sante.senegal.entities.Disponibilite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteResponseDto {

    private Long idDisponibilite;

    // Informations du médecin
    private Long idMedecin;
    private String nomMedecin;
    private String prenomMedecin;
    private String specialiteMedecin;

    // Informations du service
    private Long idService;
    private String nomService;
    private String descriptionService;

    // Informations de l'hôpital
    private Long idHopital;
    private String nomHopital;
    private String adresseHopital;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private DayOfWeek jourSemaine;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private Disponibilite.StatutDisponibilite statut;
    private String motifIndisponibilite;
    private Disponibilite.Recurrence recurrence;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFinRecurrence;

    // Champs calculés
    private Integer dureeEnMinutes;
    private Boolean estAujourdhui;
    private Boolean estPassee;
}