package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.MedicamentPrescritDto;
import com.sante.senegal.dto.PrescriptionDto;
import com.sante.senegal.entities.MedicamentPrescrit;

import java.util.List;

public interface PrescriptionService {
    PrescriptionDto creerPrescription(Long consultationId, Integer dureeTraitement, String instructionsGenerales);

    MedicamentPrescritDto ajouterMedicament(Long prescriptionId, String nomMedicament, String dosage, String frequence,
                                            Integer duree, String instructionsSpecifiques, Integer quantite);

    MedicamentPrescritDto mettreAJourStatutMedicament(Long medicamentId, MedicamentPrescrit.StatutMedicament statut);

    PrescriptionDto terminerPrescription(Long prescriptionId);

    List<PrescriptionDto> getPrescriptionsActives(Long patientId);

    PrescriptionDto getPrescriptionById(Long id);
}