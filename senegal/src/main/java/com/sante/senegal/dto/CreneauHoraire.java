package com.sante.senegal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class CreneauHoraire {
    private LocalTime heureDebut;
    private LocalTime heureFin;
}

