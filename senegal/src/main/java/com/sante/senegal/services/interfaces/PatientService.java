// ===============================
// INTERFACES DES SERVICES
// ===============================

package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;

import java.util.List;


// ===============================
// SERVICE PATIENT
// ===============================
public interface PatientService {

    /**
     * Inscription d'un patient (auto-inscription publique)
     */
    AuthResponseDto inscrirePatient(InscriptionPatientDto dto);

    /**
     * Consultation du profil patient
     */
    UtilisateurDetailDto consulterProfil(String email);

    /**
     * Modification du profil par le patient lui-même
     */
    AuthResponseDto modifierProfil(Long userId, ModificationProfilPatientDto dto);

    /**
     * Changement de mot de passe
     */
    void changerMotDePasse(Long userId, ChangementMotDePasseDto dto);

    /**
     * Récupération d'un patient par ID
     */
    Patient getPatientById(Long id);
    List<Patient> getPatients();

    /**
     * Vérification de l'existence d'un email
     */
    boolean emailExiste(String email);
}





