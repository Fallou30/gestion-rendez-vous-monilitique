package com.sante.senegal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriteresRechercheDTO {
    private String typeEntite; // PATIENT, MEDECIN, CONSULTATION
    private String terme;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Map<String, String> filtres;
    private String triePar;
    private String ordresTri; // ASC, DESC

    @Override
    public String toString() {
        return String.format("Type: %s, Terme: %s, Filtres: %s",
                typeEntite, terme, filtres);
    }
}