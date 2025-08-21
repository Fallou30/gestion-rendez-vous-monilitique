package com.sante.senegal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportAsynchroneDTO {
    @NotBlank
    private String typeExport;

    @NotBlank
    private String format;

    private String email; // Pour notification
    private Map<String, Object> parametres;

    @Override
    public String toString() {
        return String.format("Type: %s, Format: %s, Email: %s",
                typeExport, format, email);
    }
}

