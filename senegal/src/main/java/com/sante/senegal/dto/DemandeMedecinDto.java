package com.sante.senegal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DemandeMedecinDto extends UtilisateurDto {
    private List<Long> idsHopitaux;
    private Long idService;
    private String specialite;
    private String numeroOrdre;
    private String titre;
    private String matricule;
    private Integer experience;
    private String biographie;
    private String diplomePath;
    private String carteOrdrePath;
    private String cvPath;
}