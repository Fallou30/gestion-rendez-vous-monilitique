package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "examens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen")
    private Long idExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consultation", referencedColumnName = "id_consultation")
    private Consultation consultation;

    @Column(name = "type_examen", nullable = false)
    private String typeExamen;

    @Column(name = "nom_examen", nullable = false)
    private String nomExamen;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_prescription", nullable = false)
    private LocalDate datePrescription;

    @Column(name = "date_realisation")
    private LocalDate dateRealisation;

    @Column(columnDefinition = "TEXT")
    private String resultats;

    @Column(columnDefinition = "TEXT")
    private String interpretation;

    @Enumerated(EnumType.STRING)
    private StatutExamen statut = StatutExamen.PRESCRIT;

    @Enumerated(EnumType.STRING)
    private NiveauUrgence urgence = NiveauUrgence.NORMALE;

    public enum StatutExamen {
        PRESCRIT, PROGRAMME, REALISE, ANNULE
    }

    public enum NiveauUrgence {
        NORMALE, URGENT, TRES_URGENT
    }
}
