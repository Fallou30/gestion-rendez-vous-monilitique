package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // <-- AJOUTÉ
@AllArgsConstructor
public class ProfilAdminDto {
    private String role;
    private String permissions;
}