package com.sante.senegal.dto;

import com.sante.senegal.entities.Service.StatutService;
import lombok.Data;

@Data
public class ServiceDto {
    private Long idService;
    private Long idHopital;
    private String nomHopital;

    private String nom;
    private String description;
    private String emplacement;
    private String telephone;
    private String email;
    private Integer capacitePatientsJour;

    private Long idChefService;
    private String nomChefService;
    private String prenomChefService;

    private StatutService statut;
}
