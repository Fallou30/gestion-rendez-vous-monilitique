package com.sante.senegal.dto;

import com.sante.senegal.entities.DossierMedical;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DossierMedicalDto {
    private Long idDossier;
    private Long patientId;
    private String patientNomComplet;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private DossierMedical.StatutDossier statut;
    private String antecedentsMedicaux;
    private String antecedentsFamiliaux;
    private String vaccinations;
    private String notesGenerales;
}
