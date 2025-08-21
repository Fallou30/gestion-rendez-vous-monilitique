package com.sante.senegal.dto;

import com.sante.senegal.entities.Utilisateur;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter les détails d'un utilisateur, incluant les informations spécifiques aux patients et aux médecins.
 */
@Data
@Builder
public class UtilisateurDetailDto {
    private String nom;
    private String prenom;
    private String email;
    private Utilisateur.TypeUtilisateur type;
    private Utilisateur.StatutUtilisateur statut;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Utilisateur.Sexe sexe;
    private String adresse;
    private String telephone;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime dateDerniereConnexion;
    // Champs spécifiques aux patients
    private String profession;
    private String groupeSanguin;
    private String allergies;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String preferencesNotification;
    // Champs spécifiques aux réceptionnistes
    private String poste;
    private Long idHopital;
    // Champs spécifiques aux médecins
    private String titre;
    private String specialite;
    private String matricule;
    private List<Long> idshopitaux;
    private String biographie;
    private String numeroOrdre;
    private Integer experience;
    // Champs spécifiques aux médecins et aux réceptionnistes
    private Long idService;
    // Admin et Super Admin
    private String role;
    private String permissions;
}