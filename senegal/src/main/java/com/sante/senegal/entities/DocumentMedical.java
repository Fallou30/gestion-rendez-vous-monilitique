package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "documents_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMedical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dossier", referencedColumnName = "id_dossier")
    private DossierMedical dossier;

    @Column(name = "type_document", length = 100)
    private String typeDocument;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private StatutDocument statut = StatutDocument.ACTIF;

    public enum StatutDocument {
        ACTIF, ARCHIVE, SUPPRIME
    }
}