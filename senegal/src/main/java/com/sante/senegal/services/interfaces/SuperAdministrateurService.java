package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.*;

// ===============================
// SERVICE SUPER ADMINISTRATEUR
// ===============================
public interface SuperAdministrateurService {

    /**
     * Création d'un administrateur (par super admin)
     */
    AuthResponseDto creerAdministrateur(CreationAdminDto dto);

    /**
     * Modification d'un administrateur
     */
    AuthResponseDto modifierAdministrateur(Long id, ModificationAdminDto dto);

    /**
     * Suppression d'un administrateur
     */
    void supprimerAdministrateur(Long id);

    /**
     * Consultation du profil administrateur
     */
    UtilisateurDetailDto consulterProfil(String email);

    /**
     * Modification du profil par l'administrateur lui-même
     */
    AuthResponseDto modifierProfil(Long userId, ModificationProfilDto dto);

    /**
     * Changement de mot de passe
     */
    void changerMotDePasse(Long userId, ChangementMotDePasseDto dto);
}