package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "planning")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planning")
    private Long idPlanning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medecin", nullable = false)
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hopital", nullable = false)
    private Hopital hopital;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "reserve", nullable = false)
    private boolean reserve = false;

    @OneToOne
    @JoinColumn(name = "id_rendez_vous")
    private RendezVous rendezVous;
}
