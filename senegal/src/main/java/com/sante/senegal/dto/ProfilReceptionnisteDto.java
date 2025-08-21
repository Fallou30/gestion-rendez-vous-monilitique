package com.sante.senegal.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // <-- AJOUTÉ
@AllArgsConstructor
public class ProfilReceptionnisteDto {
    private String poste;
    private Long idHopital;
    private Long idService;
}
