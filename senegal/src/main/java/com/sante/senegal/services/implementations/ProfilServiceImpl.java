package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.*;

import com.sante.senegal.services.interfaces.ProfilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ProfilServiceImpl implements ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final ReceptionnisteRepository receptionnisteRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public ProfilUtilisateurDto consulterProfil(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        ProfilUtilisateurDto.ProfilUtilisateurDtoBuilder builder = ProfilUtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .type(utilisateur.getType())
                .statut(utilisateur.getStatut())
                .dateNaissance(utilisateur.getDateNaissance())
                .lieuNaissance(utilisateur.getLieuNaissance())
                .sexe(utilisateur.getSexe())
                .adresse(utilisateur.getAdresse())
                .telephone(utilisateur.getTelephone())
                .dateCreation(utilisateur.getDateCreation())
                .dateModification(utilisateur.getDateModification());

        switch (utilisateur.getType()) {
            case MEDECIN:
                Medecin medecin = medecinRepository.findById(userId).orElseThrow();
                builder.medecin(modelMapper.map(medecin, ProfilMedecinDto.class));
                break;
            case PATIENT:
                Patient patient = patientRepository.findById(userId).orElseThrow();
                builder.patient(modelMapper.map(patient, ProfilPatientDto.class));
                break;
            case RECEPTIONNISTE:
                Receptionniste receptionniste = receptionnisteRepository.findById(userId).orElseThrow();
                builder.receptionniste(modelMapper.map(receptionniste, ProfilReceptionnisteDto.class));
                break;
            case ADMIN:
            case SUPER_ADMIN:
                Administrateur admin = administrateurRepository.findById(userId).orElseThrow();
                builder.admin(modelMapper.map(admin, ProfilAdminDto.class));
                break;
        }

        return builder.build();
    }

    @Override
    public ProfilUtilisateurDto modifierProfil(Long userId, ProfilUtilisateurDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Mise à jour des champs communs
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setDateNaissance(dto.getDateNaissance());
        utilisateur.setLieuNaissance(dto.getLieuNaissance());
        utilisateur.setSexe(dto.getSexe());
        utilisateur.setAdresse(dto.getAdresse());
        utilisateur.setTelephone(dto.getTelephone());

        // Mise à jour des champs spécifiques
        switch (utilisateur.getType()) {
            case MEDECIN:
                Medecin medecin = medecinRepository.findById(userId).orElseThrow();
                modelMapper.map(dto.getMedecin(), medecin);
                medecinRepository.save(medecin);
                break;
            case PATIENT:
                Patient patient = patientRepository.findById(userId).orElseThrow();
                modelMapper.map(dto.getPatient(), patient);
                patientRepository.save(patient);
                break;
            case RECEPTIONNISTE:
                Receptionniste receptionniste = receptionnisteRepository.findById(userId).orElseThrow();
                modelMapper.map(dto.getReceptionniste(), receptionniste);
                receptionnisteRepository.save(receptionniste);
                break;
            case ADMIN:
            case SUPER_ADMIN:
                Administrateur admin = administrateurRepository.findById(userId).orElseThrow();
                modelMapper.map(dto.getAdmin(), admin);
                administrateurRepository.save(admin);
                break;
        }

        utilisateurRepository.save(utilisateur);
        return consulterProfil(userId);
    }

    @Override
    public void changerMotDePasse(Long userId, ChangementMotDePasseDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Vérification de l'ancien mot de passe
        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new InscriptionException("Ancien mot de passe incorrect");
        }

        // Vérification de la correspondance des nouveaux mots de passe
        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            throw new InscriptionException("Les nouveaux mots de passe ne correspondent pas");
        }

        // Mise à jour du mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
    }
}