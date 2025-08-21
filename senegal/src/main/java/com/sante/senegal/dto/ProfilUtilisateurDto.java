package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor // <-- AJOUTÉ
@AllArgsConstructor // <-- AJOUTÉ
public class ProfilUtilisateurDto {
    // Informations de base
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Utilisateur.TypeUtilisateur type;
    private Utilisateur.StatutUtilisateur statut;

    // Informations personnelles
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Utilisateur.Sexe sexe;
    private String adresse;
    private String telephone;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Champs spécifiques par type
    private ProfilMedecinDto medecin;
    private ProfilPatientDto patient;
    private ProfilReceptionnisteDto receptionniste;
    private ProfilAdminDto admin;
}
