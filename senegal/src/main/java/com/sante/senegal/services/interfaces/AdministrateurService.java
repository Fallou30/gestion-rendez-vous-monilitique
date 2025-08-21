package com.sante.senegal.services.interfaces;


import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.entities.Utilisateur;

import java.util.List;

// ===============================
// SERVICE ADMINISTRATEUR
// ===============================
public interface AdministrateurService {

    /**
     * Création d'un médecin par un administrateur
     */
    AuthResponseDto creerMedecin(CreationMedecinDto dto);

    /**
     * Création d'un nouveau médecin (statut MEDECIN_NOUVEAU)
     */
    AuthResponseDto creerMedecinNouveau(NouveauMedecinDto dto);

    /**
     * Validation d'une demande d'inscription médecin
     */
    AuthResponseDto validerDemandeInscription(Long idMedecin, boolean approuver, String commentaire);

    /**
     * Liste des demandes d'inscription de médecins en attente
     */
    List<Medecin> listerDemandesValidation();

    /**
     * Inscription d'un réceptionniste
     */
    AuthResponseDto inscrireReceptionniste(InscriptionReceptionnisteDto dto);

    /**
     * Modification d'un patient
     */
    AuthResponseDto modifierPatient(Long id, ModificationPatientDto dto);

    /**
     * Modification d'un médecin
     */
    AuthResponseDto modifierMedecin(Long id, ModificationMedecinDto dto);

    /**
     * Modification d'un réceptionniste
     */
    AuthResponseDto modifierReceptionniste(Long id, ModificationReceptionnisteDto dto);

    /**
     * Suppression d'un patient (désactivation)
     */
    void supprimerPatient(Long id);

    /**
     * Suppression d'un médecin (désactivation)
     */
    void supprimerMedecin(Long id);

    /**
     * Suppression d'un réceptionniste (désactivation)
     */
    void supprimerReceptionniste(Long id);


    /**
     * Suppression définitive d'un utilisateur
     */
    void supprimerDefinitivement(Long id);
    List<UtilisateurDto> listerUtilisateurs(Utilisateur.TypeUtilisateur type, Utilisateur.StatutUtilisateur statut);
    UtilisateurDto changerStatutUtilisateur(Long id, Utilisateur.StatutUtilisateur statut);
    AuthResponseDto modifierAdministrateur(Long id, ModificationAdminDto dto);

}
