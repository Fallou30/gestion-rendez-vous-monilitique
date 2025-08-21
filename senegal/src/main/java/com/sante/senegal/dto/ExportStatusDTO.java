package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportStatusDTO {
    private String exportId;
    private String statut; // EN_COURS, TERMINE, ERREUR
    private int progression; // 0-100
    private String message;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String urlTelechargement;
}

