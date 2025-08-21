package com.sante.senegal.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    private LocalDate dateNaissance;

    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @NotBlank
    @Size(min = 8)
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @CreationTimestamp
    @Column(updatable = true)
    private LocalDateTime dateModification;

    private LocalDateTime dateDerniereConnexion;

    @Enumerated(EnumType.STRING)
    private StatutUtilisateur statut = StatutUtilisateur.ACTIF;

    @Enumerated(EnumType.STRING)
    private TypeUtilisateur type;

    public enum Sexe {
        MASCULIN, FEMININ
    }

    public enum StatutUtilisateur {
        ACTIF, INACTIF, SUSPENDU, SUPPRIME
    }

    public enum TypeUtilisateur {
       SUPER_ADMIN, ADMIN, MEDECIN, MEDECIN_NOUVEAU, PATIENT, RECEPTIONNISTE, TECHNICIEN
    }
}