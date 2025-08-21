package com.sante.senegal.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.sante.senegal.entities.Utilisateur;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InscriptionBaseDto {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Utilisateur.Sexe sexe;
    private String adresse;
    private String telephone;
    private String email;
    private String motDePasse;
    private String confirmationMotDePasse;

    // Validation personnalisée des mots de passe
    public boolean isMotDePasseValide() {
        return motDePasse != null && motDePasse.equals(confirmationMotDePasse);
    }
}