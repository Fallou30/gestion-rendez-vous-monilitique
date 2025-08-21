package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disponibilite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilite")
    private Long idDisponibilite;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine")
    private DayOfWeek jourSemaine;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    private StatutDisponibilite statut = StatutDisponibilite.DISPONIBLE;

    @Column(name = "motif_indisponibilite", columnDefinition = "TEXT")
    private String motifIndisponibilite;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Recurrence recurrence;

    @Column(name = "date_fin_recurrence")
    private LocalDate dateFinRecurrence;

    public enum StatutDisponibilite {
        DISPONIBLE, OCCUPE, INDISPONIBLE
    }

    public enum Recurrence {
        PONCTUELLE,
        HEBDOMADAIRE,
        MENSUELLE
    }
}
