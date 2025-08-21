package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.*;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.services.interfaces.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ===============================
// SERVICE PATIENT IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto inscrirePatient(InscriptionPatientDto dto) {
        log.info("Début inscription patient pour email: {}", dto.getEmail());

        validerDonneesBase(dto);

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new InscriptionException("Un compte avec cet email existe déjà");
        }

        Patient patient = Patient.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .lieuNaissance(dto.getLieuNaissance())
                .sexe(dto.getSexe())
                .profession(dto.getProfession())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getMotDePasse()))
                .type(Utilisateur.TypeUtilisateur.PATIENT)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .groupeSanguin(dto.getGroupeSanguin())
                .allergies(dto.getAllergies())
                .contactUrgenceNom(dto.getContactUrgenceNom())
                .contactUrgenceTelephone(dto.getContactUrgenceTelephone())
                .preferencesNotification(dto.getPreferencesNotification())
                .build();

        Patient patientSauvegarde = patientRepository.save(patient);

        DossierMedical dossier = DossierMedical.builder()
                .patient(patientSauvegarde)
                .statut(DossierMedical.StatutDossier.ACTIF)
                .build();

        patientSauvegarde.setDossierMedical(dossier);

        String token = jwtService.generateToken(patientSauvegarde);

        try {
            emailService.envoyerEmailBienvenue(patientSauvegarde);
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de l'email de bienvenue: {}", e.getMessage());
        }

        log.info("Patient inscrit avec succès: {}", patientSauvegarde.getId());
        return new AuthResponseDto(token, patientSauvegarde);
    }

    @Override
    public UtilisateurDetailDto consulterProfil(String email) {
        Patient patient = (Patient) utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new InscriptionException("Patient non trouvé"));
        return convertToDetailedDto(patient);
    }

    @Override
    public AuthResponseDto modifierProfil(Long userId, ModificationProfilPatientDto dto) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Patient non trouvé"));

        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setSexe(dto.getSexe());
        patient.setAdresse(dto.getAdresse());
        patient.setTelephone(dto.getTelephone());
        patient.setGroupeSanguin(dto.getGroupeSanguin());
        patient.setAllergies(dto.getAllergies());
        patient.setPreferencesNotification(dto.getPreferenceNotification());
        Patient updated = patientRepository.save(patient);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public void changerMotDePasse(Long userId, ChangementMotDePasseDto dto) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Patient non trouvé"));

        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), patient.getMotDePasse())) {
            throw new InscriptionException("Ancien mot de passe incorrect");
        }

        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            throw new InscriptionException("Les nouveaux mots de passe ne correspondent pas");
        }

        patient.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        patientRepository.save(patient);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient avec l'id " + id + " non trouvé"));
    }

    @Override
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    private void validerDonneesBase(InscriptionBaseDto dto) {
        if (!dto.isMotDePasseValide()) {
            throw new InscriptionException("Les mots de passe ne correspondent pas");
        }
    }

    private UtilisateurDetailDto convertToDetailedDto(Patient patient) {
        return UtilisateurDetailDto.builder()
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .email(patient.getEmail())
                .type(patient.getType())
                .statut(patient.getStatut())
                .dateNaissance(patient.getDateNaissance())
                .sexe(patient.getSexe())
                .adresse(patient.getAdresse())
                .telephone(patient.getTelephone())
                .groupeSanguin(patient.getGroupeSanguin())
                .allergies(patient.getAllergies())
                .contactUrgenceNom(patient.getContactUrgenceNom())
                .contactUrgenceTelephone(patient.getContactUrgenceTelephone())
                .preferencesNotification(patient.getPreferencesNotification())
                .build();
    }
    @Override
    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

}



