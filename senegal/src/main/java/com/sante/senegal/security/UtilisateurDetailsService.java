package com.sante.senegal.security;

import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Log pour débogage
        System.out.println("Recherche de l'utilisateur avec email: " + email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email.toLowerCase().trim()) // Normalisation de l'email
                .orElseThrow(() -> {
                    System.out.println("Aucun utilisateur trouvé pour email: " + email);
                    return new UsernameNotFoundException("Identifiants invalides");
                });

        System.out.println("Utilisateur trouvé: " + utilisateur.getEmail() + ", statut: " + utilisateur.getStatut());

        // Mise à jour de la date de dernière connexion
        utilisateur.setDateDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        return new CustomUserDetails(utilisateur);
    }
}
