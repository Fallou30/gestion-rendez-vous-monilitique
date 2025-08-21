package com.sante.senegal.services.implementations;


import com.sante.senegal.dto.AuthResponseDto;
import com.sante.senegal.dto.ChangementMotDePasseDto;
import com.sante.senegal.dto.ModificationProfilDto;
import com.sante.senegal.dto.UtilisateurDetailDto;
import com.sante.senegal.entities.Receptionniste;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.ReceptionnisteRepository;
import com.sante.senegal.repositories.UtilisateurRepository;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.services.interfaces.ReceptionnisteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// ===============================
// SERVICE RECEPTIONNISTE IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReceptionnisteServiceImpl implements ReceptionnisteService {

    private final ReceptionnisteRepository receptionnisteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UtilisateurDetailDto consulterProfil(String email) {
        Receptionniste receptionniste = (Receptionniste) utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new InscriptionException("Réceptionniste non trouvé"));
        return convertToDetailedDto(receptionniste);
    }

    @Override
    public AuthResponseDto modifierProfil(Long userId, ModificationProfilDto dto) {
        Receptionniste receptionniste = receptionnisteRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Réceptionniste non trouvé"));

        receptionniste.setNom(dto.getNom());
        receptionniste.setPrenom(dto.getPrenom());
        receptionniste.setDateNaissance(dto.getDateNaissance());
        receptionniste.setLieuNaissance(dto.getLieuNaissance());
        receptionniste.setSexe(dto.getSexe());
        receptionniste.setAdresse(dto.getAdresse());
        receptionniste.setTelephone(dto.getTelephone());
        receptionniste.setDateModification(LocalDateTime.now());
        Receptionniste updated = receptionnisteRepository.save(receptionniste);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public void changerMotDePasse(Long userId, ChangementMotDePasseDto dto) {
        Receptionniste receptionniste = receptionnisteRepository.findById(userId)
                .orElseThrow(() -> new InscriptionException("Réceptionniste non trouvé"));

        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), receptionniste.getMotDePasse())) {
            throw new InscriptionException("Ancien mot de passe incorrect");
        }

        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmationMotDePasse())) {
            throw new InscriptionException("Les nouveaux mots de passe ne correspondent pas");
        }

        receptionniste.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        receptionnisteRepository.save(receptionniste);

        log.info("Mot de passe changé pour réceptionniste {}", userId);
    }

    private UtilisateurDetailDto convertToDetailedDto(Receptionniste receptionniste) {
        return UtilisateurDetailDto.builder()
                .nom(receptionniste.getNom())
                .prenom(receptionniste.getPrenom())
                .email(receptionniste.getEmail())
                .type(receptionniste.getType())
                .statut(receptionniste.getStatut())
                .dateNaissance(receptionniste.getDateNaissance())
                .lieuNaissance(receptionniste.getLieuNaissance())
                .sexe(receptionniste.getSexe())
                .adresse(receptionniste.getAdresse())
                .telephone(receptionniste.getTelephone())
                .idService(receptionniste.getService() != null ? receptionniste.getService().getIdService() : null)
                .idHopital(receptionniste.getHopital() != null ? receptionniste.getHopital().getIdHopital() : null)
                .poste(receptionniste.getPoste())
                .build();
    }
}
