package com.sante.senegal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NouveauMedecinDto {
    @NotBlank
    private String email;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String telephone;
}
