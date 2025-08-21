package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.DashboardMedecinStatDto;
import com.sante.senegal.dto.MedicamentPrescritStat;
import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Prescription;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.MedicamentPrescritRepository;
import com.sante.senegal.repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiquesService {

    private final ConsultationRepository consultationRepository;

    private final PrescriptionRepository prescriptionRepository;

    private final MedicamentPrescritRepository medicamentPrescritRepository;

    /**
     * Statistiques des consultations par médecin
     */
    public Map<String, Object> getStatistiquesConsultationsMedecin(Long medecinId,
                                                                   LocalDate dateDebut,
                                                                   LocalDate dateFin) {
        Map<String, Object> stats = new HashMap<>();

        List<Consultation> consultations = consultationRepository
                .findByRendezVousMedecinIdAndDateHeureBetween(
                        medecinId, dateDebut.atStartOfDay(), dateFin.atTime(23, 59, 59));

        stats.put("totalConsultations", consultations.size());
        stats.put("consultationsTerminees", consultations.stream()
                .filter(c -> c.getStatut() == Consultation.StatutConsultation.TERMINEE)
                .count());
        stats.put("dureeMovenne", consultations.stream()
                .filter(c -> c.getDureeReelle() != null)
                .mapToInt(Consultation::getDureeReelle)
                .average()
                .orElse(0.0));

        return stats;
    }

    /**
     * Statistiques des prescriptions
     */
    public Map<String, Object> getStatistiquesPrescriptions(LocalDate dateDebut,
                                                            LocalDate dateFin) {
        Map<String, Object> stats = new HashMap<>();

        List<Prescription> prescriptions = prescriptionRepository
                .findByDatePrescriptionBetween(dateDebut, dateFin);

        stats.put("totalPrescriptions", prescriptions.size());
        stats.put("prescriptionsActives", prescriptions.stream()
                .filter(p -> p.getStatut() == Prescription.StatutPrescription.ACTIVE)
                .count());
        stats.put("prescriptionsTerminees", prescriptions.stream()
                .filter(p -> p.getStatut() == Prescription.StatutPrescription.TERMINEE)
                .count());

        return stats;
    }

    /**
     * Top des médicaments les plus prescrits
     */
//    public List<Map<String, Object>> getTopMedicamentsPrescrits(int limit) {
//
//        return medicamentPrescritRepository.findTopMedicamentsPrescrits(limit);
//    }
    public List<MedicamentPrescritStat> getTopMedicamentsPrescrits(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return medicamentPrescritRepository.findTopMedicamentsPrescrits(pageable);
    }

    public DashboardMedecinStatDto getDashboardStatsMedecin(Long medecinId) {
        LocalDate today = LocalDate.now();

        List<Consultation> consultationsDuJour = consultationRepository
                .findByRendezVousMedecinIdAndDateHeureBetween(
                        medecinId,
                        today.atStartOfDay(),
                        today.atTime(23, 59, 59));

        long total = consultationsDuJour.size();
        double dureeMoyenne = consultationsDuJour.stream()
                .filter(c -> c.getDureeReelle() != null)
                .mapToInt(Consultation::getDureeReelle)
                .average()
                .orElse(0.0);

        double satisfaction = consultationsDuJour.stream()
                .filter(c -> c.getSatisfaction() != null)
                .mapToDouble(Consultation::getSatisfaction)
                .average()
                .orElse(0.0);

        return new DashboardMedecinStatDto(total, dureeMoyenne, satisfaction);
    }

}