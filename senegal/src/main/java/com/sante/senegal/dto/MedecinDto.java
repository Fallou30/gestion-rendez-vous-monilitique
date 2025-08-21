package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class MedecinDto extends UtilisateurDto {
    private List<Long> idsHopitaux;
    private Long idService;
    private String specialite;
    private String numeroOrdre;
    private String titre;
    private String matricule;
    private Integer experience;
    private String biographie;
}
