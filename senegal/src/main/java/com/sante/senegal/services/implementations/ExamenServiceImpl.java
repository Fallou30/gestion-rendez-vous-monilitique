package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Examen;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.ExamenRepository;
import com.sante.senegal.services.interfaces.ExamenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamenServiceImpl implements ExamenService {

    private final ExamenRepository examenRepository;
    private final ConsultationRepository consultationRepository;
    private final ModelMapper modelMapper;

    @Override
    public ExamenDto prescrireExamen(CreateExamenRequest createExamenDto) {
        Consultation consultation = consultationRepository.findById(createExamenDto.getConsultationId())
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));

        Examen examen = modelMapper.map(createExamenDto, Examen.class);
        examen.setConsultation(consultation);
        examen.setDatePrescription(LocalDate.now());
        examen.setStatut(Examen.StatutExamen.PRESCRIT);

        return modelMapper.map(examenRepository.save(examen), ExamenDto.class);
    }

    @Override
    public ExamenDto programmerExamen(Long examenId, LocalDate dateRealisation) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new EntityNotFoundException("Examen non trouvé"));

        examen.setDateRealisation(dateRealisation);
        examen.setStatut(Examen.StatutExamen.PROGRAMME);

        return modelMapper.map(examenRepository.save(examen), ExamenDto.class);
    }

    @Override
    public ExamenDto saisirResultats(Long examenId,ExamenResultatsRequest resultatExamenDto) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new EntityNotFoundException("Examen non trouvé"));

        modelMapper.map(resultatExamenDto, examen);
        examen.setStatut(Examen.StatutExamen.REALISE);

        return modelMapper.map(examenRepository.save(examen), ExamenDto.class);
    }

    @Override
    public List<ExamenDto> getExamensPatient(Long patientId) {
        return examenRepository.findByConsultationDossierPatientIdOrderByDatePrescriptionDesc(patientId).stream()
                .map(examen -> modelMapper.map(examen, ExamenDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamenDto> getExamensEnAttente() {
        return examenRepository.findByStatutIn(Arrays.asList(
                        Examen.StatutExamen.PRESCRIT,
                        Examen.StatutExamen.PROGRAMME
                )).stream()
                .map(examen -> modelMapper.map(examen, ExamenDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamenDto> getExamensUrgents() {
        return examenRepository.findByUrgenceInAndStatutNot(
                        Arrays.asList(Examen.NiveauUrgence.URGENT, Examen.NiveauUrgence.TRES_URGENT),
                        Examen.StatutExamen.REALISE
                ).stream()
                .map(examen -> modelMapper.map(examen, ExamenDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ExamenDto getExamenById(Long id) {
        return modelMapper.map(examenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Examen non trouvé")), ExamenDto.class);
    }
}