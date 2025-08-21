package com.sante.senegal.services.interfaces;


import com.sante.senegal.dto.AuthResponseDto;
import com.sante.senegal.dto.LoginRequestDto;
import com.sante.senegal.entities.Utilisateur;

import java.util.Optional;

// ===============================
// SERVICE AUTHENTIFICATION (COMMUN)
// ===============================
public interface AuthService {

    /**
     * Login utilisateur
     */
    AuthResponseDto login(LoginRequestDto request);

    /**
     * Récupération d'un utilisateur par ID
     */
    Optional<Utilisateur> getUserById(Long id);

    /**
     * Vérification de l'existence d'un email
     */
    boolean emailExiste(String email);
}