package com.sante.senegal.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PersonnelMedicalDto extends UtilisateurDto {
    private Long idHopital;
    private Long idService;
    private String poste;
}