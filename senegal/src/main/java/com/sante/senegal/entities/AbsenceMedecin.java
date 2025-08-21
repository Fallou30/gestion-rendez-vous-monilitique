package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "absences_medecins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsenceMedecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_absence")
    private Long idAbsence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medecin", nullable = false)
    private Medecin medecin;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Motif motif;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public enum Motif {
        CONGE_ANNUEL,
        MALADIE,
        FORMATION,
        MISSION,
        AUTRE
    }
}
