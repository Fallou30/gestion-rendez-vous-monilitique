package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.MedicamentPrescritDto;
import com.sante.senegal.dto.PrescriptionDto;
import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.MedicamentPrescrit;
import com.sante.senegal.entities.Prescription;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.MedicamentPrescritRepository;
import com.sante.senegal.repositories.PrescriptionRepository;
import com.sante.senegal.services.interfaces.PrescriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicamentPrescritRepository medicamentPrescritRepository;
    private final ConsultationRepository consultationRepository;
    private final ModelMapper modelMapper;

    @Override
    public PrescriptionDto creerPrescription(Long consultationId, Integer dureeTraitement, String instructionsGenerales) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation non trouvée"));

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .datePrescription(LocalDate.now())
                .dureeTraitement(dureeTraitement)
                .instructionsGenerales(instructionsGenerales)
                .statut(Prescription.StatutPrescription.ACTIVE)
                .build();

        return modelMapper.map(prescriptionRepository.save(prescription), PrescriptionDto.class);
    }

    @Override
    public MedicamentPrescritDto ajouterMedicament(Long prescriptionId, String nomMedicament, String dosage,
                                                   String frequence, Integer duree, String instructionsSpecifiques,
                                                   Integer quantite) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription non trouvée"));

        MedicamentPrescrit medicament = MedicamentPrescrit.builder()
                .prescription(prescription)
                .nomMedicament(nomMedicament)
                .dosage(dosage)
                .frequence(frequence)
                .duree(duree)
                .instructionsSpecifiques(instructionsSpecifiques)
                .quantitePrescrite(quantite)
                .statut(MedicamentPrescrit.StatutMedicament.PRESCRIT)
                .build();

        return modelMapper.map(medicamentPrescritRepository.save(medicament), MedicamentPrescritDto.class);
    }

    @Override
    public MedicamentPrescritDto mettreAJourStatutMedicament(Long medicamentId, MedicamentPrescrit.StatutMedicament statut) {
        MedicamentPrescrit medicament = medicamentPrescritRepository.findById(medicamentId)
                .orElseThrow(() -> new EntityNotFoundException("Médicament non trouvé"));

        medicament.setStatut(MedicamentPrescrit.StatutMedicament.valueOf(statut.name()));
        return modelMapper.map(medicamentPrescritRepository.save(medicament), MedicamentPrescritDto.class);
    }

    @Override
    public PrescriptionDto terminerPrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription non trouvée"));

        prescription.setStatut(Prescription.StatutPrescription.TERMINEE);

        prescription.getMedicaments().forEach(medicament -> {
            if (medicament.getStatut() != MedicamentPrescrit.StatutMedicament.ANNULE) {
                medicament.setStatut(MedicamentPrescrit.StatutMedicament.TERMINE);
            }
        });

        return modelMapper.map(prescriptionRepository.save(prescription), PrescriptionDto.class);
    }

    @Override
    public List<PrescriptionDto> getPrescriptionsActives(Long patientId) {
        return prescriptionRepository.findByConsultationDossierPatientIdAndStatut(
                        patientId, Prescription.StatutPrescription.ACTIVE).stream()
                .map(p -> modelMapper.map(p, PrescriptionDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PrescriptionDto getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription non trouvée"));
        return modelMapper.map(prescription, PrescriptionDto.class);
    }
}