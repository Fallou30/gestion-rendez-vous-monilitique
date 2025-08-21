package com.sante.senegal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportPersonnaliseDTO {
    @NotBlank
    private String typeExport;

    @NotBlank
    private String format; // csv, excel, pdf, json

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<String> colonnes;
    private Map<String, String> filtres;

    @Override
    public String toString() {
        return String.format("Type: %s, Format: %s, Filtres: %s",
                typeExport, format, filtres);
    }
}
