package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consultation")
    private Long idConsultation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rdv", referencedColumnName = "id_rdv")
    private RendezVous rendezVous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dossier", referencedColumnName = "id_dossier")
    private DossierMedical dossier;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "duree_reelle")
    private Integer dureeReelle;

    @Column(columnDefinition = "TEXT")
    private String symptomes;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    @Enumerated(EnumType.STRING)
    private StatutConsultation statut = StatutConsultation.PROGRAMMEE;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
    private Double satisfaction; // permet de savoir l'avis du patient sur la consultation
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relations
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Examen> examens = new ArrayList<>();

    public enum StatutConsultation {
        PROGRAMMEE, EN_COURS, TERMINEE, ANNULEE
    }
}
