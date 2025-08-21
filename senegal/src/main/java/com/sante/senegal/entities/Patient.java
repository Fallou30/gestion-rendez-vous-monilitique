package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patients")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends Utilisateur {
    @Column(name = "num_assurance", unique = true)
    private String numAssurance;

    @Column(name = "groupe_sanguin", length = 5)
    private String groupeSanguin;

    @Column(columnDefinition = "TEXT")
    private String allergies;
    @Column(name = "contact_urgence_nom", length = 100)
    private String contactUrgenceNom;

    @Column(name = "contact_urgence_telephone", length = 20)
    private String contactUrgenceTelephone;
    @Column(name = "profession", length = 50)
    private String profession;
    @Column(name = "preferences_notification", columnDefinition = "TEXT")
    private String preferencesNotification;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private DossierMedical dossierMedical;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();
}