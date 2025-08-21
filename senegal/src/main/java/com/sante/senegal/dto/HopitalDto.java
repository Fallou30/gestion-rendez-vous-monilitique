package com.sante.senegal.dto;

import com.sante.senegal.entities.Hopital.StatutHopital;
import lombok.Data;

@Data
public class HopitalDto {
    private Long idHopital;
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
    private StatutHopital statut;
}
