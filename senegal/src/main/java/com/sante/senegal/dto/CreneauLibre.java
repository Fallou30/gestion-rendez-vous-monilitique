package com.sante.senegal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class CreneauLibre {
    private LocalTime heureDebut;
    private LocalTime heureFin;
}