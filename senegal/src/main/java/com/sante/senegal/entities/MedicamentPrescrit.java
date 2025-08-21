package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicaments_prescrits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentPrescrit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicament_prescrit")
    private Long idMedicamentPrescrit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prescription", referencedColumnName = "id_prescription")
    private Prescription prescription;

    @Column(name = "nom_medicament", nullable = false)
    private String nomMedicament;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private String frequence;

    @Column(nullable = false)
    private Integer duree;

    @Column(name = "instructions_specifiques", columnDefinition = "TEXT")
    private String instructionsSpecifiques;

    @Column(name = "quantite_prescrite")
    private Integer quantitePrescrite;

    @Enumerated(EnumType.STRING)
    private StatutMedicament statut = StatutMedicament.PRESCRIT;

    public enum StatutMedicament {
        PRESCRIT, DELIVRE, TERMINE, ANNULE
    }
}