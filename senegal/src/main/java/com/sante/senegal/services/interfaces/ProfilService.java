package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.ProfilUtilisateurDto;
import com.sante.senegal.dto.ChangementMotDePasseDto;

public interface ProfilService {
    ProfilUtilisateurDto consulterProfil(Long userId);
    ProfilUtilisateurDto modifierProfil(Long userId, ProfilUtilisateurDto dto);
    void changerMotDePasse(Long userId, ChangementMotDePasseDto dto);
}