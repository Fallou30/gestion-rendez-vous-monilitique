package com.sante.senegal.dto;

import com.sante.senegal.entities.Examen;
import lombok.Data;

@Data
public class CreateExamenRequest {
    private Long consultationId;
    private String typeExamen;
    private String nomExamen;
    private String description;
    private Examen.NiveauUrgence urgence;
}

