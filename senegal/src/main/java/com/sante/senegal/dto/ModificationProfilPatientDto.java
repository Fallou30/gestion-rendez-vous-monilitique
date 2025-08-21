package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

// ModificationProfilPatientDto.java
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ModificationProfilPatientDto extends ModificationProfilDto {
    private String groupeSanguin;
    private String allergies;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String preferenceNotification;
}