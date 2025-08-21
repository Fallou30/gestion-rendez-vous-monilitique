package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dossiers_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dossier")
    private Long idDossier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient", referencedColumnName = "id")
    private Patient patient;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Enumerated(EnumType.STRING)
    private StatutDossier statut = StatutDossier.ACTIF;

    @Column(name = "antecedents_medicaux", columnDefinition = "TEXT")
    private String antecedentsMedicaux;

    @Column(name = "antecedents_familiaux", columnDefinition = "TEXT")
    private String antecedentsFamiliaux;

    @Column(columnDefinition = "TEXT")
    private String vaccinations;

    @Column(name = "notes_generales", columnDefinition = "TEXT")
    private String notesGenerales;

    // Relations
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentMedical> documents = new ArrayList<>();

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    public enum StatutDossier {
        ACTIF, ARCHIVE, SUPPRIME
    }
}
