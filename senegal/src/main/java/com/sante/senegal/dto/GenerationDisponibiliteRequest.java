package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationDisponibiliteRequest {
    private Long idMedecin;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Map<Long, List<CreneauHoraire>> creneauxParHopital; // Map<idHopital, List<CreneauHoraire>>
    private Map<Long, List<DayOfWeek>> joursParHopital; // Map<idHopital, List<DayOfWeek>>
}