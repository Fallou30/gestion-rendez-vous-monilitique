package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class UtilisateurDto {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Utilisateur.Sexe sexe;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateDerniereConnexion;
    private Utilisateur.StatutUtilisateur statut;
    private Utilisateur.TypeUtilisateur type;
}
