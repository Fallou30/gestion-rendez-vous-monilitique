package com.sante.senegal.dto;
import com.sante.senegal.entities.RendezVous;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class RendezVousDto {
    private Long idRdv;
    private Long patientId;
    private String patientNomComplet;
    private Long medecinId;
    private String medecinNomComplet;
    private String medecinSpecialite;
    private Long serviceId;
    private String serviceNom;
    private Long hopitalId;
    private String hopitalNom;
    private String adresseHopital;
    private String villeHopital;
    private String regionHopital;
    private LocalDateTime dateHeure;
    private Integer dureePrevue;
    private RendezVous.TypeConsultation typeConsultation;
    private String motif;
    private RendezVous.NiveauUrgence niveauUrgence;
    private RendezVous.StatutRendezVous statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String modePriseRdv;
}
