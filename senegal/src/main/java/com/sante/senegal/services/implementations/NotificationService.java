package com.sante.senegal.services.implementations;

import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Examen;
import com.sante.senegal.entities.Prescription;
import com.sante.senegal.entities.RendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class NotificationService {

    private final Logger logger =  LoggerFactory.getLogger(NotificationService.class);

    public void notifierRendezVousConfirme(RendezVous rendezVous) {
        // Logique de notification par email/SMS
        logger.info("Notification : Rendez-vous confirmé pour le patient avec le Dr "
        );
    }

    public void notifierConsultationTerminee(Consultation consultation) {
        logger.info("Notification : Consultation terminée pour le patient "
        );
    }

    public void notifierPrescriptionEmise(Prescription prescription) {
        logger.info("Notification : Prescription émise pour le patient "
        );
    }

    public void notifierExamenPrescrit(Examen examen) {
        logger.info("Notification : Examen prescrit pour le patient "
        );
    }

    public void notifierExamenUrgent(Examen examen) {
        logger.warn ("URGENT : Examen urgent prescrit  pour le patient "
        );
    }
    public void notifierRappelRendezVous(RendezVous rendezVous) {
        logger.info("Notification : Rappel de rendez-vous pour le patient "
        );
    }
}