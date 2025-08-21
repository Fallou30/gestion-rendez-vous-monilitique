package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Examen;

import java.time.LocalDate;
import java.util.List;
public interface ExamenService {
    ExamenDto prescrireExamen(CreateExamenRequest createExamenDto);
    ExamenDto programmerExamen(Long examenId, LocalDate dateRealisation);
    ExamenDto saisirResultats(Long examenId, ExamenResultatsRequest resultatExamenDto);
    List<ExamenDto> getExamensPatient(Long patientId);
    List<ExamenDto> getExamensEnAttente();
    List<ExamenDto> getExamensUrgents();
    ExamenDto getExamenById(Long id);
}