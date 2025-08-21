package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PatientDto extends UtilisateurDto {
    private String numAssurance;
    private String groupeSanguin;
    private String allergies;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String profession;
    private String preferencesNotification;

    // Si besoin, on peut ajouter une version simplifiée du DossierMedical ou juste l’ID
    private Long idDossierMedical;

    // Pour éviter la surcharge, on n’inclut pas la liste des rendez-vous ici
}
