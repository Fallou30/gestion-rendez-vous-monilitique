package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "hopitaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hopital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hopital")
    private Long idHopital;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(length = 100)
    private String ville;

    @Column(length = 100)
    private String region;

    @Column(length = 20)
    private String telephone;

    private String email;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(name = "coordonnees_gps")
    private String coordonneesGps;

    @Column(name = "heures_ouverture", columnDefinition = "TEXT")
    private String heuresOuverture;

    @Column(name = "type_etablissement", length = 100)
    private String typeEtablissement;

    @Column(name = "capacite_lits")
    private Integer capaciteLits;

    @Enumerated(EnumType.STRING)
    private StatutHopital statut = StatutHopital.ACTIF;

    // Relations
    @ManyToMany(mappedBy = "hopitaux", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Medecin> medecins = new ArrayList<>();
    @OneToMany(mappedBy = "hopital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "hopital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonnelMedical> personnelMedical = new ArrayList<>();

    @OneToMany(mappedBy = "hopital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    public enum StatutHopital {
        ACTIF, INACTIF, MAINTENANCE
    }
}