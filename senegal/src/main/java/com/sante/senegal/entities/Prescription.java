package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prescription")
    private Long idPrescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consultation", referencedColumnName = "id_consultation")
    private Consultation consultation;

    @Column(name = "date_prescription", nullable = false)
    private LocalDate datePrescription;

    @Column(name = "duree_traitement")
    private Integer dureeTraitement;

    @Column(name = "instructions_generales", columnDefinition = "TEXT")
    private String instructionsGenerales;

    @Enumerated(EnumType.STRING)
    private StatutPrescription statut = StatutPrescription.ACTIVE;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relations
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicamentPrescrit> medicaments = new ArrayList<>();

    public enum StatutPrescription {
        ACTIVE, TERMINEE, ANNULEE
    }
}
