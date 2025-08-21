package com.sante.senegal.dto;

import com.sante.senegal.entities.RendezVous;
import lombok.Data;

@Data
public class PlanningReservationRequestDto {
    private Long idPlanning;
    private Long idPatient;
    private RendezVous.TypeConsultation typeConsultation;
    private String motif;
}
