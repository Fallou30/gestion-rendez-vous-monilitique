/**
 * Ce package sera utilisé plus tart pour optimiser
 * Pour le moment on charge entité avec fetch = FetchType.EAGER
 *//*

package com.sante.senegal.mappers;

import com.sante.senegal.dto.RendezVousDTO;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.entities.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {

    RendezVousMapper INSTANCE = Mappers.getMapper(RendezVousMapper.class);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient", target = "patientNomComplet", qualifiedByName = "patientToNomComplet")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "medecin", target = "medecinNomComplet", qualifiedByName = "medecinToNomComplet")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.nom", target = "serviceNom")
    @Mapping(source = "hopital.id", target = "hopitalId")
    @Mapping(source = "hopital.nom", target = "hopitalNom")
    @Mapping(source = "hopital.adresse", target = "adresseHopital")
    @Mapping(source = "hopital.ville", target = "villeHopital")
    @Mapping(source = "hopital.region", target = "regionHopital")
    RendezVousDTO rendezVousToRendezVousDTO(RendezVous rendezVous);

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "hopital", ignore = true)
    RendezVous rendezVousDTOToRendezVous(RendezVousDTO rendezVousDTO);

    @Named("patientToNomComplet")
    default String patientToNomComplet(Patient patient) {
        if (patient == null) {
            return null;
        }
        return patient.getPrenom() + " " + patient.getNom();
    }

    @Named("medecinToNomComplet")
    default String medecinToNomComplet(Medecin medecin) {
        if (medecin == null) {
            return null;
        }
        return "Dr. " + medecin.getPrenom() + " " + medecin.getNom();
    }

    // Méthode pour mettre à jour une entité existante
    default void updateRendezVousFromDTO(RendezVousDTO dto, RendezVous entity) {
        if (dto == null) {
            return;
        }

        entity.setDateHeure(dto.getDateHeure());
        entity.setDureePrevue(dto.getDureePrevue());
        entity.setTypeConsultation(dto.getTypeConsultation());
        entity.setMotif(dto.getMotif());
        entity.setNiveauUrgence(dto.getNiveauUrgence());
        entity.setStatut(dto.getStatut());
        entity.setNotesPatient(dto.getNotesPatient());
        entity.setNotesMedecin(dto.getNotesMedecin());
        entity.setModePriseRdv(dto.getModePriseRdv());
        entity.setDateModification(LocalDateTime.now());
    }
}*/
