package com.sante.senegal.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ModificationAdminDto extends UtilisateurDto {
    private String role;
    private Set<String> permissions;
}