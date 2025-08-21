package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medecins")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Medecin extends Utilisateur {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "medecin_hopital",
            joinColumns = @JoinColumn(name = "id_medecin"),
            inverseJoinColumns = @JoinColumn(name = "id_hopital")
    )
    private List<Hopital> hopitaux = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id_service")
    private Service service;

    @Column(length = 100)
    private String specialite;
    @Column(name = "numero_ordre", unique = true)
    private String numeroOrdre;
    private String matricule;
    @Column(length = 50)
    private String titre;
    private Integer experience;
    @Column(columnDefinition = "TEXT")
    private String biographie;

    // Champs pour les documents justificatifs
    private String diplomePath;
    private String carteOrdrePath;
    private String cvPath;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilite> disponibilites = new ArrayList<>();
}