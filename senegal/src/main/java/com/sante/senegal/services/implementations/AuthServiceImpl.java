package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.AuthResponseDto;
import com.sante.senegal.dto.LoginRequestDto;

import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.repositories.UtilisateurRepository;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

// ===============================
// SERVICE AUTHENTIFICATION IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        log.info("Tentative de connexion pour email: {}", request.getEmail());

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadCredentialsException("Email requis");
        }

        if (request.getMotDePasse() == null || request.getMotDePasse().trim().isEmpty()) {
            throw new BadCredentialsException("Mot de passe requis");
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BadCredentialsException("Mot de passe incorrect");
        }

        // Vérifier le statut de l'utilisateur
        if (utilisateur.getStatut() == Utilisateur.StatutUtilisateur.SUPPRIME) {
            throw new BadCredentialsException("Compte supprimé");
        }

        if (utilisateur.getStatut() == Utilisateur.StatutUtilisateur.SUSPENDU) {
            throw new BadCredentialsException("Compte suspendu");
        }

        if (utilisateur.getStatut() == Utilisateur.StatutUtilisateur.INACTIF) {
            throw new BadCredentialsException("Compte inactif");
        }

        // Mettre à jour la dernière connexion
        utilisateur.setDateDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(utilisateur);

        log.info("Connexion réussie pour utilisateur: {} (type: {})",
                utilisateur.getEmail(), utilisateur.getType());

        return new AuthResponseDto(token, utilisateur);
    }

    @Override
    public Optional<Utilisateur> getUserById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Override
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}