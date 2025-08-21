package com.sante.senegal.services.implementations;


import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Hopital;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.exceptions.FileStorageException;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.HopitalRepository;
import com.sante.senegal.repositories.MedecinRepository;
import com.sante.senegal.repositories.ServiceRepository;
import com.sante.senegal.repositories.UtilisateurRepository;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.services.interfaces.EmailService;
import com.sante.senegal.services.interfaces.FileStorageService;
import com.sante.senegal.services.interfaces.MedecinService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

// ===============================
// SERVICE MEDECIN IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HopitalRepository hopitalRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;

    @Override
    public AuthResponseDto demanderInscriptionMedecin(DemandeMedecinDto dto,
                                                      MultipartFile cv,
                                                      MultipartFile diplome,
                                                      MultipartFile carteOrdre) throws FileStorageException {
        log.info("Soumission de demande d'inscription par médecin existant: {}", dto.getEmail());

        Medecin medecin = medecinRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InscriptionException("Aucun médecin avec cet email n'a été trouvé"));

        if (!Utilisateur.TypeUtilisateur.MEDECIN.equals(medecin.getType())
                || !Utilisateur.StatutUtilisateur.ACTIF.equals(medecin.getStatut())) {
            throw new InscriptionException("Vous n'êtes pas autorisé à faire une demande");
        }

        List<Hopital> hopitaux = hopitalRepository.findAllById(dto.getIdsHopitaux());
        if (hopitaux.isEmpty()) {
            throw new InscriptionException("Aucun hôpital valide fourni");
        }

        com.sante.senegal.entities.Service service = serviceRepository.findById(dto.getIdService())
                .orElseThrow(() -> new InscriptionException("Service non trouvé"));

        String cvPath = cv != null ? fileStorageService.storeFile(cv, "cv") : null;
        String diplomePath = diplome != null ? fileStorageService.storeFile(diplome, "diplome") : null;
        String carteOrdrePath = carteOrdre != null ? fileStorageService.storeFile(carteOrdre, "carteOrdre") : null;

        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setLieuNaissance(dto.getLieuNaissance());
        medecin.setAdresse(dto.getAdresse());
        medecin.setTelephone(dto.getTelephone());
        medecin.setSpecialite(dto.getSpecialite());
        medecin.setNumeroOrdre(dto.getNumeroOrdre());
        medecin.setMatricule(dto.getMatricule());
        medecin.setTitre(dto.getTitre());
        medecin.setExperience(dto.getExperience());
        medecin.setBiographie(dto.getBiographie());
        medecin.setHopitaux(hopitaux);
        medecin.setService(service);
        medecin.setCvPath(cvPath);
        medecin.setDiplomePath(diplomePath);
        medecin.setCarteOrdrePath(carteOrdrePath);
        medecin.setStatut(Utilisateur.StatutUtilisateur.INACTIF);

        Medecin saved = medecinRepository.save(medecin);
        Utilisateur user = utilisateurRepository.findByType(Utilisateur.TypeUtilisateur.SUPER_ADMIN);
        emailService.notifierNouvelleDemandeInscription(user);

        log.info("Demande enregistrée avec succès pour médecin id {}", saved.getId());
        return new AuthResponseDto(jwtService.generateToken(saved), saved);
    }

    @Override
    public UtilisateurDetailDto consulterProfil(String email) {
        Medecin medecin = (Medecin) utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));
        return convertToDetailedDto(medecin);
    }

    @Override
    public AuthResponseDto modifierProfil(Long userId, ModificationProfilMedecinDto dto) {
        Medecin medecin = medecinRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));

        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setSexe(dto.getSexe());
        medecin.setAdresse(dto.getAdresse());
        medecin.setTelephone(dto.getTelephone());
        medecin.setSpecialite(dto.getSpecialite());
        medecin.setBiographie(dto.getBiographie());

        Medecin updated = medecinRepository.save(medecin);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public void changerMotDePasse(Long userId, ChangementMotDePasseDto dto) {
        Medecin medecin = medecinRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));

        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), medecin.getMotDePasse())) {
            throw new InscriptionException("Ancien mot de passe incorrect");
        }

        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            throw new InscriptionException("Les nouveaux mots de passe ne correspondent pas");
        }

        medecin.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        medecinRepository.save(medecin);
    }

    @Override
    public Medecin updateMedecinDocument(Long idMedecin, MultipartFile file, String type) throws FileStorageException {
        Medecin medecin = medecinRepository.findById(idMedecin)
                .orElseThrow(() -> new RuntimeException("Médecin introuvable avec ID: " + idMedecin));

        String oldFilePath = switch (type) {
            case "cv" -> medecin.getCvPath();
            case "diplome" -> medecin.getDiplomePath();
            case "carteOrdre" -> medecin.getCarteOrdrePath();
            default -> throw new IllegalArgumentException("Type de document invalide");
        };

        if (oldFilePath != null && !oldFilePath.isBlank()) {
            fileStorageService.deleteFile(oldFilePath);
        }

        String newFilePath = fileStorageService.storeFile(file, type);

        switch (type) {
            case "cv" -> medecin.setCvPath(newFilePath);
            case "diplome" -> medecin.setDiplomePath(newFilePath);
            case "carteOrdre" -> medecin.setCarteOrdrePath(newFilePath);
        }

        return medecinRepository.save(medecin);
    }

    @Override
    public Medecin getMedecinById(Long id) {
        return medecinRepository.findById(id).orElseThrow();
    }
    @Override
    public UtilisateurDetailDto modifierProfil(Long idMedecin, UtilisateurDetailDto dto) {
        Medecin medecin = medecinRepository.findById(idMedecin)
                .orElseThrow(() -> new EntityNotFoundException("Médecin introuvable"));

        // Mise à jour des champs simples depuis le DTO
        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setEmail(dto.getEmail());
        medecin.setTelephone(dto.getTelephone());


        medecinRepository.save(medecin);

        return modelMapper.map(medecin, UtilisateurDetailDto.class);
    }
    @Override
    public UtilisateurDetailDto consulterProfil(Long idMedecin) {
        Medecin medecin = medecinRepository.findById(idMedecin)
                .orElseThrow(() -> new EntityNotFoundException("Médecin introuvable"));

        return modelMapper.map(medecin, UtilisateurDetailDto.class);
    }


    @Override
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    private UtilisateurDetailDto convertToDetailedDto(Medecin medecin) {
        return UtilisateurDetailDto.builder()
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .email(medecin.getEmail())
                .type(medecin.getType())
                .statut(medecin.getStatut())
                .dateNaissance(medecin.getDateNaissance())
                .sexe(medecin.getSexe())
                .adresse(medecin.getAdresse())
                .telephone(medecin.getTelephone())
                .specialite(medecin.getSpecialite())
                .matricule(medecin.getMatricule())
                .idshopitaux(medecin.getHopitaux().stream()
                        .map(Hopital::getIdHopital)
                        .collect(Collectors.toList()))
                .biographie(medecin.getBiographie())
                .experience(medecin.getExperience())
                .numeroOrdre(medecin.getNumeroOrdre())
                .titre(medecin.getTitre())
                .build();
    }
    @Override
    public List<MedecinByServiceDto> getMedecinsByService(Long serviceId) {
        com.sante.senegal.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        return medecinRepository.findByServiceId(serviceId).stream()
                .map(medecin -> {
                    MedecinByServiceDto dto = modelMapper.map(medecin, MedecinByServiceDto.class);
                    dto.setServiceName(service.getNom());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MedecinByHopitalDto> getMedecinsByHopital(Long hopitalId) {
        Hopital hopital = hopitalRepository.findById(hopitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));

        return medecinRepository.findByHopitalId(hopitalId).stream()
                .map(medecin -> {
                    MedecinByHopitalDto dto = modelMapper.map(medecin, MedecinByHopitalDto.class);
                    dto.setHopitalName(hopital.getNom());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MedecinDto> getMedecinsByServiceAndHopital(Long serviceId, Long hopitalId) {
        return medecinRepository.findByServiceAndHopital(serviceId, hopitalId).stream()
                .map(medecin -> modelMapper.map(medecin, MedecinDto.class))
                .collect(Collectors.toList());
    }
}
