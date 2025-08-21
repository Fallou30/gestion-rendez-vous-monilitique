package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangerStatutDto {
    private Utilisateur.StatutUtilisateur statut;
}
