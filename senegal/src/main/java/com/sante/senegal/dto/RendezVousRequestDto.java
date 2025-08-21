package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousRequestDto {
    private LocalDateTime dateHeure;
    private Integer dureePrevue;
    private String motif;
    private String typeConsultation;
    private String niveauUrgence;
    private Long idPatient;
    private Long idMedecin;
    private Long idService;
    private Long idHopital;
}
