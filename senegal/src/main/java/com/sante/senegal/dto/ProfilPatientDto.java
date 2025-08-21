package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // <-- AJOUTÉ
@AllArgsConstructor
public class ProfilPatientDto {
    private String profession;
    private String groupeSanguin;
    private String allergies;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String preferencesNotification;
}