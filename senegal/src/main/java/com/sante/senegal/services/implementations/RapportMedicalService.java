package com.sante.senegal.services.implementations;

import com.sante.senegal.entities.Consultation;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.ExamenRepository;
import com.sante.senegal.repositories.PrescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RapportMedicalService {


    private final ConsultationRepository consultationRepository;


    private final PrescriptionRepository prescriptionRepository;


    private final ExamenRepository examenRepository;

    /**
     * Générer un rapport de consultation
     */
    public Map<String, Object> genererRapportConsultation(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("consultation", consultation);
        rapport.put("patient", consultation.getDossier().getPatient());
        rapport.put("medecin", consultation.getRendezVous().getMedecin());
        rapport.put("prescriptions", prescriptionRepository.findByConsultation(consultation));
        rapport.put("examens", examenRepository.findByConsultation(consultation));
        rapport.put("dateGeneration", LocalDateTime.now());

        return rapport;
    }

    /**
     * Générer un rapport de suivi patient
     */
    public Map<String, Object> genererRapportSuiviPatient(Long patientId, LocalDate dateDebut, LocalDate dateFin) {
        List<Consultation> consultations = consultationRepository
                .findByPatientIdBetweenDates(patientId, dateDebut, dateFin);

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("patientId", patientId);
        rapport.put("periode", Map.of("debut", dateDebut, "fin", dateFin));
        rapport.put("nombreConsultations", consultations.size());
        rapport.put("consultations", consultations);
        rapport.put("prescriptionsTotal", consultations.stream()
                .mapToInt(prescriptionRepository::countByConsultation)
                .sum());
        rapport.put("examensTotal", consultations.stream()
                .mapToInt(c -> examenRepository.countByConsultation(c))
                .sum());

        return rapport;
    }

    /**
     * Générer statistiques médicales
     */
    public Map<String, Object> genererStatistiquesMedicales(LocalDate dateDebut, LocalDate dateFin) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("consultationsParJour", consultationRepository
                .countConsultationsParJour(dateDebut, dateFin));
        stats.put("prescriptionsParMedecin", prescriptionRepository
                .countPrescriptionsParMedecin(dateDebut, dateFin));
        stats.put("examensParType", examenRepository
                .countBetweenDates(dateDebut, dateFin));
        stats.put("periode", Map.of("debut", dateDebut, "fin", dateFin));
        stats.put("dateGeneration", LocalDateTime.now());

        return stats;
    }
}