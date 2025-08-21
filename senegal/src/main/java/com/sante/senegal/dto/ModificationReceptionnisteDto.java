package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModificationReceptionnisteDto extends PersonnelMedicalDto {

}
