package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMedecinStatDto {
    private long consultationsJour;
    private double dureeMoyenne;
    private double tauxSatisfaction;
}

