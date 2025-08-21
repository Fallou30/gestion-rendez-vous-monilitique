package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Administrateur;
import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.AdministrateurRepository;
import com.sante.senegal.repositories.UtilisateurRepository;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.security.PasswordService;
import com.sante.senegal.services.interfaces.EmailService;
import com.sante.senegal.services.interfaces.SuperAdministrateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ===============================
// SERVICE SUPER ADMINISTRATEUR IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SuperAdministrateurServiceImpl implements SuperAdministrateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto creerAdministrateur(CreationAdminDto dto) {
        log.info("Création administrateur par super admin pour email: {}", dto.getEmail());

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new InscriptionException("Un compte avec cet email existe déjà");
        }

        String motDePasseTemp = passwordService.genererMotDePasseTemporaire();

        Administrateur administrateur = Administrateur.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(motDePasseTemp))
                .type(Utilisateur.TypeUtilisateur.ADMIN)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .permissions(dto.getPermissions())
                .build();

        Administrateur saved = administrateurRepository.save(administrateur);

        try {
            emailService.envoyerAccesInitiaux(saved, motDePasseTemp);
        } catch (Exception e) {
            log.warn("Erreur envoi email administrateur: {}", e.getMessage());
        }

        String token = jwtService.generateToken(saved);
        return new AuthResponseDto(token, saved);
    }

    @Override
    public AuthResponseDto modifierAdministrateur(Long id, ModificationAdminDto dto) {
        Administrateur administrateur = administrateurRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Administrateur non trouvé"));

        administrateur.setNom(dto.getNom());
        administrateur.setPrenom(dto.getPrenom());
        administrateur.setDateNaissance(dto.getDateNaissance());
        administrateur.setSexe(dto.getSexe());
        administrateur.setAdresse(dto.getAdresse());
        administrateur.setTelephone(dto.getTelephone());
        administrateur.setEmail(dto.getEmail());

        Administrateur updated = administrateurRepository.save(administrateur);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public void supprimerAdministrateur(Long id) {
        Administrateur administrateur = administrateurRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Administrateur non trouvé"));

        administrateur.setStatut(Utilisateur.StatutUtilisateur.SUPPRIME);
        administrateurRepository.save(administrateur);

        log.info("Administrateur {} désactivé", id);
    }

    @Override
    public UtilisateurDetailDto consulterProfil(String email) {
        Administrateur administrateur = (Administrateur) utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new InscriptionException("Administrateur non trouvé"));
        return convertToDetailedDto(administrateur);
    }

    @Override
    public AuthResponseDto modifierProfil(Long userId, ModificationProfilDto dto) {
        Administrateur administrateur = administrateurRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Administrateur non trouvé"));

        administrateur.setNom(dto.getNom());
        administrateur.setPrenom(dto.getPrenom());
        administrateur.setDateNaissance(dto.getDateNaissance());
        administrateur.setSexe(dto.getSexe());
        administrateur.setAdresse(dto.getAdresse());
        administrateur.setTelephone(dto.getTelephone());

        Administrateur updated = administrateurRepository.save(administrateur);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public void changerMotDePasse(Long userId, ChangementMotDePasseDto dto) {
        Administrateur administrateur = administrateurRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Administrateur non trouvé"));

        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), administrateur.getMotDePasse())) {
            throw new InscriptionException("Ancien mot de passe incorrect");
        }

        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            throw new InscriptionException("Les nouveaux mots de passe ne correspondent pas");
        }

        administrateur.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        administrateurRepository.save(administrateur);

        log.info("Mot de passe changé pour administrateur {}", userId);
    }

    private UtilisateurDetailDto convertToDetailedDto(Administrateur administrateur) {
        return UtilisateurDetailDto.builder()
                .nom(administrateur.getNom())
                .prenom(administrateur.getPrenom())
                .email(administrateur.getEmail())
                .type(administrateur.getType())
                .statut(administrateur.getStatut())
                .dateNaissance(administrateur.getDateNaissance())
                .sexe(administrateur.getSexe())
                .adresse(administrateur.getAdresse())
                .telephone(administrateur.getTelephone())
                .build();
    }
}