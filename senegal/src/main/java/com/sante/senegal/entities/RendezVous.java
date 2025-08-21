package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rdv")
    private Long idRdv;

   // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.LAZY) // pour le moment remplacer plus tard FetchType.LAZY)
    @JoinColumn(name = "id_patient", referencedColumnName = "id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medecin", referencedColumnName = "id")
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service", referencedColumnName = "id_service")
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hopital", referencedColumnName = "id_hopital")
    private Hopital hopital;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "duree_prevue")
    private Integer dureePrevue;

    @Column(name = "type_consultation", length = 100)
    @Enumerated(EnumType.STRING)
    private TypeConsultation typeConsultation;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_urgence")
    private NiveauUrgence niveauUrgence = NiveauUrgence.NORMALE;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut = StatutRendezVous.PROGRAMME;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_prise_rdv", length = 30)
    private ModePriseRdv modePriseRdv;


    // Relations
    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
    private Consultation consultation;

    public enum NiveauUrgence {
        NORMALE, URGENT, TRES_URGENT
    }

    public enum StatutRendezVous {
        PROGRAMME, CONFIRME, EN_COURS, TERMINE, ANNULE, REPORTE
    }
    public enum TypeConsultation {
        CONSULTATION_GENERALE,
        CONSULTATION_SPECIALISTE,
        CONSULTATION_URGENCE,
        CONSULTATION_SUIVI,
        CONSULTATION_PREMIERE
    }

    public enum ModePriseRdv {
        EN_LIGNE,
        TELEPHONE,
        SECRETARIAT,
        AUTRE
    }
}

