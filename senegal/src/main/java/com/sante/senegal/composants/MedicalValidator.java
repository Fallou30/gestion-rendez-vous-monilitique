package com.sante.senegal.composants;

import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.DossierMedical;
import com.sante.senegal.entities.MedicamentPrescrit;
import com.sante.senegal.entities.Prescription;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MedicalValidator {

    public void validerDossierMedical(DossierMedical dossier) {
        if (dossier.getPatient() == null) {
            throw new ValidationException("Un dossier médical doit être associé à un patient");
        }
    }

    public void validerConsultation(Consultation consultation) {
        if (consultation.getRendezVous() == null) {
            throw new ValidationException("Une consultation doit être associée à un rendez-vous");
        }
        if (consultation.getDossier() == null) {
            throw new ValidationException("Une consultation doit être associée à un dossier médical");
        }
    }

    public void validerPrescription(Prescription prescription) {
        if (prescription.getConsultation() == null) {
            throw new ValidationException("Une prescription doit être associée à une consultation");
        }
        if (prescription.getDureeTraitement() != null && prescription.getDureeTraitement() <= 0) {
            throw new ValidationException("La durée du traitement doit être positive");
        }
    }

    public void validerMedicament(MedicamentPrescrit medicament) {
        if (medicament.getNomMedicament() == null || medicament.getNomMedicament().trim().isEmpty()) {
            throw new ValidationException("Le nom du médicament est obligatoire");
        }
        if (medicament.getDosage() == null || medicament.getDosage().trim().isEmpty()) {
            throw new ValidationException("Le dosage du médicament est obligatoire");
        }
        if (medicament.getFrequence() == null || medicament.getFrequence().trim().isEmpty()) {
            throw new ValidationException("La fréquence du médicament est obligatoire");
        }
        if (medicament.getDuree() == null || medicament.getDuree() <= 0) {
            throw new ValidationException("La durée du traitement doit être positive");
        }
    }
}