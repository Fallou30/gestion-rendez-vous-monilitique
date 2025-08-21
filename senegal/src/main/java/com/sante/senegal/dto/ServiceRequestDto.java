package com.sante.senegal.dto;

import lombok.Data;

@Data
public class ServiceRequestDto {
    private Long idHopital;
    private String nom;
    private String description;
    private String emplacement;
    private String telephone;
    private String email;
    private Integer capacitePatientsJour;
    private Long idChefService;
}
