package com.sante.senegal.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
 public class ModificationProfilMedecinDto extends ModificationProfilDto {
    private String specialite;
    private String biographie;
    private String numeroOrdre;
    private String titre;
    private String matricule;
    private String experience;
}