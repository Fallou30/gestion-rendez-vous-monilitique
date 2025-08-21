package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModificationMedecinDto extends UtilisateurDto {
    private List<Long> idsHopitaux;
    private Long idService;
    private String specialite;
    private String numeroOrdre;
    private String matricule;
    private String titre;
    private Integer experience;
    private String biographie;
    private String diplomePath;
    private String carteOrdrePath;
    private String cvPath;
}