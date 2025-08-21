package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private AuthUserDto utilisateur;

    public AuthResponseDto(String token, Utilisateur user) {
        this.token = token;
        this.utilisateur = new AuthUserDto(
                user.getId(),
                user.getEmail(),
                user.getType(),
                user.getPrenom(),
                user.getNom()
        );
    }
}