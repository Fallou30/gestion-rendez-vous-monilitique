package com.sante.senegal.services.implementations;


import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.repositories.*;
import com.sante.senegal.security.JwtService;
import com.sante.senegal.security.PasswordService;
import com.sante.senegal.services.interfaces.AdministrateurService;
import com.sante.senegal.services.interfaces.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



// ===============================
// SERVICE ADMINISTRATEUR IMPLEMENTATION
// ===============================
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdministrateurServiceImpl implements AdministrateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final HopitalRepository hopitalRepository;
    private final ServiceRepository serviceRepository;
    private final ReceptionnisteRepository receptionnisteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final ModelMapper mapper;

    @Override
    public AuthResponseDto creerMedecin(CreationMedecinDto dto) {
        log.info("Création médecin par admin pour email: {}", dto.getEmail());

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new InscriptionException("Un compte avec cet email existe déjà");
        }

        List<Hopital> hopitaux = hopitalRepository.findAllById(dto.getIdsHopitaux());
        if (hopitaux.isEmpty()) {
            throw new InscriptionException("Aucun hôpital valide fourni");
        }

        com.sante.senegal.entities.Service service = serviceRepository.findById(dto.getIdService())
                .orElseThrow(() -> new InscriptionException("Service non trouvé"));

        String motDePasseTemp = passwordService.genererMotDePasseTemporaire();

        Medecin medecin = Medecin.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .lieuNaissance(dto.getLieuNaissance())
                .sexe(dto.getSexe())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(motDePasseTemp))
                .type(Utilisateur.TypeUtilisateur.MEDECIN)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .hopitaux(hopitaux)
                .service(service)
                .specialite(dto.getSpecialite())
                .matricule(dto.getMatricule())
                .numeroOrdre(dto.getNumeroOrdre())
                .titre(dto.getTitre())
                .experience(dto.getExperience())
                .biographie(dto.getBiographie())
                .build();

        Medecin medecinSauvegarde = medecinRepository.save(medecin);
        String token = jwtService.generateToken(medecinSauvegarde);

        emailService.envoyerAccesInitiaux(medecinSauvegarde, motDePasseTemp);

        log.info("Médecin créé par admin: {}", medecinSauvegarde.getId());
        return new AuthResponseDto(token, medecinSauvegarde);
    }

    @Override
    public AuthResponseDto creerMedecinNouveau(NouveauMedecinDto dto) {
        validerEmail(dto.getEmail());

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new InscriptionException("Un compte avec cet email existe déjà");
        }

        String motDePasseTemp = passwordService.genererMotDePasseTemporaire();

        Medecin medecin = Medecin.builder()
                .email(dto.getEmail())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .telephone(dto.getTelephone())
                .motDePasse(passwordEncoder.encode(motDePasseTemp))
                .type(Utilisateur.TypeUtilisateur.MEDECIN_NOUVEAU)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .build();

        Medecin saved = medecinRepository.save(medecin);

        try {
            emailService.envoyerAccesInitiaux(saved, motDePasseTemp);
        } catch (Exception e) {
            log.warn("Erreur envoi email médecin nouveau: {}", e.getMessage());
        }

        return new AuthResponseDto(jwtService.generateToken(saved), saved);
    }

    @Override
    public AuthResponseDto validerDemandeInscription(Long idMedecin, boolean approuver, String commentaire) {
        log.info("Validation demande inscription médecin ID: {}, approuver: {}", idMedecin, approuver);

        Medecin medecin = medecinRepository.findById(idMedecin)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));

        if (medecin.getStatut() != Utilisateur.StatutUtilisateur.INACTIF) {
            throw new InscriptionException("Cette demande a déjà été traitée");
        }

        if (approuver) {
            medecin.setStatut(Utilisateur.StatutUtilisateur.ACTIF);
            medecinRepository.save(medecin);

            try {
                emailService.envoyerNotificationValidation(medecin, true, commentaire);
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi de la notification: {}", e.getMessage());
            }

            log.info("Demande d'inscription approuvée pour le médecin: {}", medecin.getId());
        } else {
            medecin.setStatut(Utilisateur.StatutUtilisateur.SUSPENDU);
            medecinRepository.save(medecin);

            try {
                emailService.envoyerNotificationValidation(medecin, false, commentaire);
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi de la notification: {}", e.getMessage());
            }

            log.info("Demande d'inscription rejetée pour le médecin: {}", medecin.getId());
        }

        String token = jwtService.generateToken(medecin);
        return new AuthResponseDto(token, medecin);
    }

    @Override
    public List<Medecin> listerDemandesValidation() {
        return medecinRepository.findByStatut(Utilisateur.StatutUtilisateur.INACTIF);
    }

    @Override
    public AuthResponseDto inscrireReceptionniste(InscriptionReceptionnisteDto dto) {
        log.info("Inscription réceptionniste pour email: {}", dto.getEmail());
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new InscriptionException("Email déjà utilisé");
        }

        Hopital hopital = hopitalRepository.findById(dto.getIdHopital())
                .orElseThrow(() -> new InscriptionException("Hôpital non trouvé"));
        com.sante.senegal.entities.Service service = serviceRepository.findById(dto.getIdService())
                .orElseThrow(() -> new InscriptionException("Service non trouvé"));
        String motDePasseTemp = passwordService.genererMotDePasseTemporaire();

        Receptionniste receptionniste = Receptionniste.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .lieuNaissance(dto.getLieuNaissance())
                .sexe(dto.getSexe())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(motDePasseTemp))
                .type(Utilisateur.TypeUtilisateur.RECEPTIONNISTE)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .hopital(hopital)
                .service(service)
                .poste(dto.getPoste())
                .build();

        Receptionniste saved = receptionnisteRepository.save(receptionniste);

        try {
            emailService.envoyerAccesInitiaux(saved, motDePasseTemp);
        } catch (Exception e) {
            log.warn("Erreur envoi email réceptionniste: {}", e.getMessage());
        }

        String token = jwtService.generateToken(saved);
        return new AuthResponseDto(token, saved);
    }

    @Override
    public AuthResponseDto modifierPatient(Long id, ModificationPatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Patient non trouvé"));

        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setLieuNaissance(dto.getLieuNaissance());
        patient.setSexe(dto.getSexe());
        patient.setAdresse(dto.getAdresse());
        patient.setTelephone(dto.getTelephone());
        patient.setEmail(dto.getEmail());
        patient.setProfession(dto.getProfession());
        patient.setNumAssurance(dto.getNumeroAssuranse());
        patient.setStatut(dto.getStatut());
        patient.setType(dto.getType());
        patient.setPreferencesNotification(dto.getPreferencesNotification());
        patient.setGroupeSanguin(dto.getGroupeSanguin());
        patient.setAllergies(dto.getAllergies());
        patient.setContactUrgenceNom(dto.getContactUrgenceNom());
        patient.setContactUrgenceTelephone(dto.getContactUrgenceTelephone());

        Patient updated = patientRepository.save(patient);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    @Override
    public AuthResponseDto modifierMedecin(Long id, ModificationMedecinDto dto) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));

        List<Hopital> hopitaux = hopitalRepository.findAllById(dto.getIdsHopitaux());
        if (hopitaux.isEmpty()) {
            throw new InscriptionException("Aucun hôpital valide fourni");
        }

        com.sante.senegal.entities.Service service = serviceRepository.findById(dto.getIdService())
                .orElseThrow(() -> new InscriptionException("Service non trouvé"));

        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setService(service);
        medecin.setHopitaux(hopitaux);
        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setLieuNaissance(dto.getLieuNaissance());
        medecin.setSexe(dto.getSexe());
        medecin.setAdresse(dto.getAdresse());
        medecin.setTelephone(dto.getTelephone());
        medecin.setEmail(dto.getEmail());
        medecin.setSpecialite(dto.getSpecialite());
        medecin.setMatricule(dto.getMatricule());
        medecin.setNumeroOrdre(dto.getNumeroOrdre());
        medecin.setTitre(dto.getTitre());
        medecin.setExperience(dto.getExperience());
        medecin.setBiographie(dto.getBiographie());

        Medecin updated = medecinRepository.save(medecin);
        String token = jwtService.generateToken(updated);
        return new AuthResponseDto(token, updated);
    }

    public AuthResponseDto modifierReceptionniste(Long id, ModificationReceptionnisteDto dto) {
        // 1. Vérifier si le réceptionniste existe
        Receptionniste receptionniste = receptionnisteRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Réceptionniste non trouvé"));

        // 2. Charger le nouvel hôpital (si modifié)
        Hopital hopital = null;
        if (dto.getIdHopital() != null) {
            hopital = hopitalRepository.findById(dto.getIdHopital())
                    .orElseThrow(() -> new InscriptionException("Hôpital non trouvé"));
        }

        // 3. Charger le nouveau service (si modifié)
        com.sante.senegal.entities.Service service = null;
        if (dto.getIdService() != null) {
            service = serviceRepository.findById(dto.getIdService())
                    .orElseThrow(() -> new InscriptionException("Service non trouvé"));
        }

        // 4. Mise à jour des champs
        receptionniste.setNom(dto.getNom());
        receptionniste.setPrenom(dto.getPrenom());
        receptionniste.setDateNaissance(dto.getDateNaissance());
        receptionniste.setLieuNaissance(dto.getLieuNaissance());
        receptionniste.setSexe(dto.getSexe());
        receptionniste.setAdresse(dto.getAdresse());
        receptionniste.setTelephone(dto.getTelephone());
        receptionniste.setEmail(dto.getEmail());

        if (hopital != null) {
            receptionniste.setHopital(hopital);
        }
        if (service != null) {
            receptionniste.setService(service);
        }
        if (dto.getPoste() != null) {
            receptionniste.setPoste(dto.getPoste());
        }

        // 5. Sauvegarde et génération du token
        Receptionniste updated = receptionnisteRepository.save(receptionniste);
        String token = jwtService.generateToken(updated);

        return new AuthResponseDto(token, updated);
    }

    @Override
    public void supprimerPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Patient non trouvé"));

        patient.setStatut(Utilisateur.StatutUtilisateur.SUPPRIME);
        patientRepository.save(patient);

        log.info("Patient {} désactivé", id);
    }

    @Override
    public void supprimerMedecin(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Médecin non trouvé"));

        medecin.setStatut(Utilisateur.StatutUtilisateur.SUPPRIME);
        medecinRepository.save(medecin);

        log.info("Médecin {} désactivé", id);
    }

    public void supprimerReceptionniste(Long id) {
        Receptionniste receptionniste = receptionnisteRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Réceptionniste non trouvé"));

        receptionniste.setStatut(Utilisateur.StatutUtilisateur.SUPPRIME);
        receptionnisteRepository.save(receptionniste);

        log.info("Réceptionniste {} désactivé", id);
    }

    @Override
    public void supprimerDefinitivement(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Utilisateur non trouvé"));

        utilisateurRepository.delete(utilisateur);
        log.info("Utilisateur {} supprimé définitivement", id);
    }
    @Override
    public AuthResponseDto modifierAdministrateur(Long id, ModificationAdminDto dto) {
        Utilisateur admin = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur introuvable avec l'ID: " + id));

        // Vérifie que c'est bien un administrateur
        if (admin.getType() != Utilisateur.TypeUtilisateur.ADMIN) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un administrateur.");
        }

        admin.setPrenom(dto.getPrenom());
        admin.setNom(dto.getNom());
        admin.setAdresse(dto.getAdresse());
        admin.setLieuNaissance(dto.getLieuNaissance());
        admin.setDateNaissance(dto.getDateNaissance());
        admin.setSexe(dto.getSexe());
        admin.setEmail(dto.getEmail());
        admin.setTelephone(dto.getTelephone());

        utilisateurRepository.save(admin);

        return mapper.map(admin, AuthResponseDto.class);
    }

    public List<UtilisateurDto> listerUtilisateurs(Utilisateur.TypeUtilisateur type, Utilisateur.StatutUtilisateur  statut) {
        Specification<Utilisateur> spec = Specification.where(null);

        if (type != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("type"), Utilisateur.TypeUtilisateur.valueOf(String.valueOf(type))));
        }

        if (statut != null ) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("statut"), Utilisateur.StatutUtilisateur.valueOf(String.valueOf(statut))));
        }

        return utilisateurRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "dateCreation"))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void validerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InscriptionException("Email requis");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InscriptionException("Format email invalide");
        }
    }

    private UtilisateurDto convertToDto(Utilisateur utilisateur) {
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .dateNaissance(utilisateur.getDateNaissance())
                .lieuNaissance(utilisateur.getLieuNaissance())
                .adresse(utilisateur.getAdresse())
                .telephone(utilisateur.getTelephone())
                .email(utilisateur.getEmail())
                .sexe(utilisateur.getSexe())
                .type(utilisateur.getType())
                .statut(utilisateur.getStatut())
                .build();
    }

    /**
     *
     */
    @Override
    public UtilisateurDto changerStatutUtilisateur(Long id, Utilisateur.StatutUtilisateur nouveauStatut) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new InscriptionException("Utilisateur non trouvé"));

        utilisateur.setStatut(nouveauStatut);
        Utilisateur misAJour = utilisateurRepository.save(utilisateur);

        log.info("Statut de l'utilisateur {} changé à {}", id, nouveauStatut);
        return convertToDto(misAJour);
    }

//    public List<UtilisateurDto>listerUtilisateurs(Utilisateur.TypeUtilisateur type, Utilisateur.StatutUtilisateur statut) {
//        return UtilisateurRepository.findByTypeAndStatut( type , statut);
//    }
}
