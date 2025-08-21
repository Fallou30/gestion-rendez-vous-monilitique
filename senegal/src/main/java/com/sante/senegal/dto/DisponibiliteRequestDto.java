package com.sante.senegal.dto;

import com.sante.senegal.entities.Disponibilite;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteRequestDto {

    @NotNull(message = "L'ID du médecin est obligatoire")
    @Positive(message = "L'ID du médecin doit être positif")
    private Long idMedecin;

    @NotNull(message = "L'ID du service est obligatoire")
    @Positive(message = "L'ID du service doit être positif")
    private Long idService;

    @NotNull(message = "L'ID de l'hôpital est obligatoire")
    @Positive(message = "L'ID de l'hôpital doit être positif")
    private Long idHopital;

    @NotNull(message = "La date est obligatoire")
    @Future(message = "La date doit être dans le futur")
    private LocalDate date;

    private DayOfWeek jourSemaine;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    private Disponibilite.StatutDisponibilite statut = Disponibilite.StatutDisponibilite.DISPONIBLE;

    @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
    private String motifIndisponibilite;

    private Disponibilite.Recurrence recurrence = Disponibilite.Recurrence.PONCTUELLE;

    @Future(message = "La date de fin de récurrence doit être dans le futur")
    private LocalDate dateFinRecurrence;

    // Validation personnalisée
    @AssertTrue(message = "L'heure de fin doit être postérieure à l'heure de début")
    private boolean isHeureFinApresHeureDebut() {
        if (heureDebut == null || heureFin == null) {
            return true; // Laisse les validations @NotNull gérer ces cas
        }
        return heureFin.isAfter(heureDebut);
    }

    @AssertTrue(message = "La date de fin de récurrence est obligatoire si une récurrence est définie")
    private boolean isDateFinRecurrenceValide() {
        if (recurrence == null || recurrence == Disponibilite.Recurrence.PONCTUELLE) {
            return true;
        }
        return dateFinRecurrence != null && !dateFinRecurrence.isBefore(date);
    }
}