package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RapportHistorique {
    private Long id;
    private String typeRapport;
    private String parametres;
    private LocalDateTime dateGeneration;
    private String utilisateur;
    private String statut;
    private String urlTelechargement;
}