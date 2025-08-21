package com.sante.senegal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ChangementMotDePasseDto {
    @NotBlank
    private String ancienMotDePasse;

    @NotBlank
    @Size(min = 8)
    private String nouveauMotDePasse;

    @NotBlank
    @Size(min = 8)
    private String confirmationMotDePasse;
}
