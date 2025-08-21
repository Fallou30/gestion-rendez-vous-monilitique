package com.sante.senegal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionPatientDto extends InscriptionBaseDto {
    private String profession;
    private String groupeSanguin;
    private String allergies;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String preferencesNotification;
}
