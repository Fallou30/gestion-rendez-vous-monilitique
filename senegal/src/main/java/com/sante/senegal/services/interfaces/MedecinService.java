package com.sante.senegal.services.interfaces;


import com.sante.senegal.dto.*;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.exceptions.FileStorageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// ===============================
// SERVICE MEDECIN
// ===============================
public interface MedecinService {

    /**
     * Demande d'inscription médecin (nécessite validation admin)
     */
    AuthResponseDto demanderInscriptionMedecin(DemandeMedecinDto dto,
                                               MultipartFile cv,
                                               MultipartFile diplome,
                                               MultipartFile carteOrdre) throws FileStorageException;

    /**
     * Consultation du profil médecin
     */
    UtilisateurDetailDto consulterProfil(String email);

    /**
     * Modification du profil par le médecin lui-même
     */
    AuthResponseDto modifierProfil(Long userId, ModificationProfilMedecinDto dto);

    /**
     * Changement de mot de passe
     */
    void changerMotDePasse(Long userId, ChangementMotDePasseDto dto);

    /**
     * Mise à jour des documents du médecin
     */
    Medecin updateMedecinDocument(Long idMedecin, MultipartFile file, String type) throws FileStorageException;

    /**
     * Récupération d'un médecin par ID
     */
    Medecin getMedecinById(Long id);

    /**
     * Vérification de l'existence d'un email
     */

    /**
     * Modifier son profile
     */
    UtilisateurDetailDto modifierProfil(Long idMedecin, UtilisateurDetailDto dto);

    /**
     * Consulter son profil
     */
    UtilisateurDetailDto consulterProfil(Long idMedecin);

    List<MedecinByServiceDto> getMedecinsByService(Long serviceId);
    List<MedecinByHopitalDto> getMedecinsByHopital(Long hopitalId);
    List<MedecinDto> getMedecinsByServiceAndHopital(Long serviceId, Long hopitalId);
    boolean emailExiste(String email);
}