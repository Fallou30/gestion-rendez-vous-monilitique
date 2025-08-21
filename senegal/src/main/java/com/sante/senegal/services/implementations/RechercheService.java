package com.sante.senegal.services.implementations;

import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.MedecinRepository;
import com.sante.senegal.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RechercheService {

    private final PatientRepository patientRepository;

    private final MedecinRepository medecinRepository;

    private final ConsultationRepository consultationRepository;

    /**
     * Rechercher des patients par critères multiples
     */
    public List<Patient> rechercherPatients(String nom, String prenom,
                                            String telephone, String email) {
        Specification<Patient> spec = Specification.where(null);

        if (nom != null && !nom.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%"));
        }

        if (prenom != null && !prenom.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("prenom")), "%" + prenom.toLowerCase() + "%"));
        }

        if (telephone != null && !telephone.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("telephone"), "%" + telephone + "%"));
        }

        if (email != null && !email.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        return patientRepository.findAll((Sort) spec);
    }

    /**
     * Rechercher des médecins par spécialité
     */
    public List<Medecin> rechercherMedecinsParSpecialite(String specialite) {
        return medecinRepository.findBySpecialiteContainingIgnoreCase(specialite);
    }

    /**
     * Rechercher des consultations par diagnostic
     */
    public List<Consultation> rechercherConsultationsParDiagnostic(String diagnostic) {
        return consultationRepository.findByDiagnosticContainingIgnoreCase(diagnostic);
    }

    /**
     * Recherche globale
     */
    public Map<String, Object> rechercheGlobale(String terme) {
        Map<String, Object> resultats = new HashMap<>();

        // Recherche dans les patients
        List<Patient> patients = patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                terme, terme);

        // Recherche dans les médecins
        List<Medecin> medecins = medecinRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                terme, terme);

        // Recherche dans les consultations
        List<Consultation> consultations = consultationRepository.findByDiagnosticContainingIgnoreCase(terme);

        resultats.put("patients", patients);
        resultats.put("medecins", medecins);
        resultats.put("consultations", consultations);
        resultats.put("totalResultats", patients.size() + medecins.size() + consultations.size());

        return resultats;
    }
}