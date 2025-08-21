package com.sante.senegal.dto;

import lombok.Data;

@Data
public class HopitalRequestDto {
    private String nom;
    private String adresse;
    private String ville;
    private String region;
    private String telephone;
    private String email;
    private String siteWeb;
    private String coordonneesGps;
    private String heuresOuverture;
    private String typeEtablissement;
    private Integer capaciteLits;
}
