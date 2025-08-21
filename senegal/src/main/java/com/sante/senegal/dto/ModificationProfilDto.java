package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;



@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public abstract class ModificationProfilDto extends UtilisateurDto {
}





