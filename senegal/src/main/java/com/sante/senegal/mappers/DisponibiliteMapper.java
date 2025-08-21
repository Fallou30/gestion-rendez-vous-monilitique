package com.sante.senegal.mappers;

import com.sante.senegal.dto.DisponibiliteRequestDto;
import com.sante.senegal.dto.DisponibiliteResponseDto;
import com.sante.senegal.entities.Disponibilite;
import com.sante.senegal.entities.Hopital;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Service;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DisponibiliteMapper {

    /**
     * Convertit un DisponibiliteRequestDto en entité Disponibilite
     */
    public Disponibilite toEntity(DisponibiliteRequestDto dto, Medecin medecin, Service service, Hopital hopital) {
        if (dto == null) {
            return null;
        }

        return Disponibilite.builder()
                .medecin(medecin)
                .service(service)
                .hopital(hopital)
                .date(dto.getDate())
                .jourSemaine(dto.getJourSemaine())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .statut(dto.getStatut())
                .motifIndisponibilite(dto.getMotifIndisponibilite())
                .recurrence(dto.getRecurrence())
                .dateFinRecurrence(dto.getDateFinRecurrence())
                .build();
    }

    /**
     * Convertit une entité Disponibilite en DisponibiliteResponseDto
     */
    public DisponibiliteResponseDto toResponseDto(Disponibilite disponibilite) {
        if (disponibilite == null) {
            return null;
        }

        return DisponibiliteResponseDto.builder()
                .idDisponibilite(disponibilite.getIdDisponibilite())
                // Médecin
                .idMedecin(disponibilite.getMedecin().getId())
                .nomMedecin(disponibilite.getMedecin().getNom())
                .prenomMedecin(disponibilite.getMedecin().getPrenom())
                .specialiteMedecin(disponibilite.getMedecin().getSpecialite())
                // Service
                .idService(disponibilite.getService().getIdService())
                .nomService(disponibilite.getService().getNom())
                .descriptionService(disponibilite.getService().getDescription())
                // Hôpital
                .idHopital(disponibilite.getHopital().getIdHopital())
                .nomHopital(disponibilite.getHopital().getNom())
                .adresseHopital(disponibilite.getHopital().getAdresse())
                // Disponibilité
                .date(disponibilite.getDate())
                .jourSemaine(disponibilite.getJourSemaine())
                .heureDebut(disponibilite.getHeureDebut())
                .heureFin(disponibilite.getHeureFin())
                .statut(disponibilite.getStatut())
                .motifIndisponibilite(disponibilite.getMotifIndisponibilite())
                .recurrence(disponibilite.getRecurrence())
                .dateFinRecurrence(disponibilite.getDateFinRecurrence())
                // Champs calculés
                .dureeEnMinutes(calculerDureeEnMinutes(disponibilite.getHeureDebut(), disponibilite.getHeureFin()))
                .estAujourdhui(disponibilite.getDate().equals(LocalDate.now()))
                .estPassee(disponibilite.getDate().isBefore(LocalDate.now()) ||
                        (disponibilite.getDate().equals(LocalDate.now()) &&
                                disponibilite.getHeureFin().isBefore(LocalTime.now())))
                .build();
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     */
    public List<DisponibiliteResponseDto> toResponseDtoList(List<Disponibilite> disponibilites) {
        if (disponibilites == null) {
            return null;
        }

        return disponibilites.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     */
    public void updateEntity(Disponibilite existingDisponibilite, DisponibiliteRequestDto dto,
                             Medecin medecin, Service service, Hopital hopital) {
        if (existingDisponibilite == null || dto == null) {
            return;
        }

        existingDisponibilite.setMedecin(medecin);
        existingDisponibilite.setService(service);
        existingDisponibilite.setHopital(hopital);
        existingDisponibilite.setDate(dto.getDate());
        existingDisponibilite.setJourSemaine(dto.getJourSemaine());
        existingDisponibilite.setHeureDebut(dto.getHeureDebut());
        existingDisponibilite.setHeureFin(dto.getHeureFin());
        existingDisponibilite.setStatut(dto.getStatut());
        existingDisponibilite.setMotifIndisponibilite(dto.getMotifIndisponibilite());
        existingDisponibilite.setRecurrence(dto.getRecurrence());
        existingDisponibilite.setDateFinRecurrence(dto.getDateFinRecurrence());
    }

    /**
     * Calcule la durée en minutes entre deux heures
     */
    private Integer calculerDureeEnMinutes(LocalTime heureDebut, LocalTime heureFin) {
        if (heureDebut == null || heureFin == null) {
            return null;
        }

        return (int) Duration.between(heureDebut, heureFin).toMinutes();
    }
}