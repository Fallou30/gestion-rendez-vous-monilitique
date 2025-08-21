package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jours_feries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jour_ferie")
    private Long idJourFerie;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TypeJourFerie type = TypeJourFerie.NATIONAL;

    @Column(length = 100)
    private String region; // Pour les jours fériés régionaux

    @Column(name = "est_recurrent")
    private Boolean estRecurrent = false;

    @Column(name = "date_creation", updatable = false)
    @CreationTimestamp
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    @UpdateTimestamp
    private LocalDateTime dateModification;

    // Pour indiquer si ce jour affecte les disponibilités
    @Column(name = "affecte_disponibilites")
    private Boolean affecteDisponibilites = true;

    // Source de synchronisation
    @Column(name = "source_api", length = 100)
    private String sourceApi;

    @Column(name = "external_id", length = 100)
    private String externalId;

    public enum TypeJourFerie {
        NATIONAL, REGIONAL, RELIGIEUX, ADMINISTRATIF
    }
}