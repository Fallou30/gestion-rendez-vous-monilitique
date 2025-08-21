package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long idService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hopital", referencedColumnName = "id_hopital")
    private Hopital hopital;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String emplacement;

    @Column(length = 20)
    private String telephone;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_service", referencedColumnName = "id")
    private Medecin chefService;

    @Column(name = "capacite_patients_jour")
    private Integer capacitePatientsJour;

    @Enumerated(EnumType.STRING)
    private StatutService statut = StatutService.ACTIF;

    // Relations
    @OneToMany(mappedBy = "service")
    private List<Medecin> medecins = new ArrayList<>();
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilite> disponibilites = new ArrayList<>();

    public enum StatutService {
        ACTIF, INACTIF, MAINTENANCE
    }
}