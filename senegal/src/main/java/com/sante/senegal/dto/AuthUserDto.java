package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import lombok.Data;

@Data
public class AuthUserDto {
    private Long id;
    private String email;
    private Utilisateur.TypeUtilisateur type;
    private String prenom;
    private String nom;

    public AuthUserDto(Long id,String email,
                       Utilisateur.TypeUtilisateur type,
                       String prenom,
                       String nom) {
        this.id=id;
        this.email = email;
        this.type = type;
        this.prenom = prenom;
        this.nom = nom;
    }
}