package com.sante.senegal.dto;

import com.sante.senegal.entities.DocumentMedical;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentMedicalDto {
    private Long idDocument;
    private Long idDossier;
    private String typeDocument;
    private String nomFichier;
    private String cheminFichier;
    private Long tailleFichier;
    private String description;
    private DocumentMedical.StatutDocument statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
