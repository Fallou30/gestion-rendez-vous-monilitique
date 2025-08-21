package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.AuthResponseDto;
import com.sante.senegal.dto.ChangementMotDePasseDto;
import com.sante.senegal.dto.ModificationProfilDto;
import com.sante.senegal.dto.UtilisateurDetailDto;

// ===============================
// SERVICE RECEPTIONNISTE
// ===============================
public interface ReceptionnisteService {

    /**
     * Consultation du profil réceptionniste
     */
    UtilisateurDetailDto consulterProfil(String email);

    /**
     * Modification du profil par le réceptionniste lui-même
     */
    AuthResponseDto modifierProfil(Long userId, ModificationProfilDto dto);

    /**
     * Changement de mot de passe
     */
    void changerMotDePasse(Long userId, ChangementMotDePasseDto dto);
}