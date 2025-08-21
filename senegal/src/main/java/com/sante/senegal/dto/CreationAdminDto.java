package com.sante.senegal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CreationAdminDto extends UtilisateurDto {
    @NotBlank(message = "Le rôle est obligatoire")
    @Size(max = 50, message = "Le rôle ne peut pas dépasser 50 caractères")
    private String role;

    private String permissions;

    @NotNull(message = "L'ID du créateur est obligatoire")
    private Long idCreateur;

    private String commentaire;
}