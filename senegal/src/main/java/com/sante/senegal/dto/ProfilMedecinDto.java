package com.sante.senegal.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor // <-- AJOUTÉ
@AllArgsConstructor
public class ProfilMedecinDto {
    private String specialite;
    private String matricule;
    private List<Long> idHopitaux;
    private String biographie;
    private String numeroOrdre;
    private Integer experience;
    private Long idService;
    private String titre;
}


