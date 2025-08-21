package com.sante.senegal.services.implementations;


import com.sante.senegal.entities.AbsenceMedecin;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.repositories.AbsenceMedecinRepository;
import com.sante.senegal.repositories.MedecinRepository;

import com.sante.senegal.services.interfaces.AbsenceMedecinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AbsenceMedecinServiceImpl implements AbsenceMedecinService {

    private final AbsenceMedecinRepository absenceMedecinRepository;
    private final MedecinRepository medecinRepository;

    @Override
    public AbsenceMedecin creerAbsence(AbsenceMedecin absence) {
        log.info("Création d'une absence pour médecin id={}", absence.getMedecin().getId());

        // Vérification si le médecin existe
        Medecin medecin = medecinRepository.findById(absence.getMedecin().getId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        absence.setMedecin(medecin);

        // Validation basique : date début <= date fin
        if (absence.getDateDebut().isAfter(absence.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure ou égale à la date de fin");
        }

        // TODO: ajouter d'autres validations métier si nécessaire (ex: chevauchement)

        AbsenceMedecin saved = absenceMedecinRepository.save(absence);

        log.info("Absence créée avec id={}", saved.getIdAbsence());

        return saved;
    }

    @Override
    public AbsenceMedecin modifierAbsence(Long id, AbsenceMedecin absenceMaj) {
        AbsenceMedecin absenceExistante = absenceMedecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée"));

        absenceExistante.setDateDebut(absenceMaj.getDateDebut());
        absenceExistante.setDateFin(absenceMaj.getDateFin());
        absenceExistante.setMotif(absenceMaj.getMotif());
        absenceExistante.setCommentaire(absenceMaj.getCommentaire());

        // Pas de changement du médecin ici, mais possible si besoin

        log.info("Modification de l'absence id={}", id);

        return absenceMedecinRepository.save(absenceExistante);
    }

    @Override
    public void supprimerAbsence(Long id) {
        if (!absenceMedecinRepository.existsById(id)) {
            throw new RuntimeException("Absence non trouvée");
        }
        absenceMedecinRepository.deleteById(id);
        log.info("Suppression de l'absence id={}", id);
    }

    @Override
    public Optional<AbsenceMedecin> getAbsenceById(Long id) {
        return absenceMedecinRepository.findById(id);
    }

    @Override
    public List<AbsenceMedecin> getAbsencesByMedecin(Long medecinId) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        return absenceMedecinRepository.findByMedecinAndDateFinAfterAndDateDebutBefore(
                medecin,
                LocalDate.now().minusYears(1), // Exemple : récupère les absences passées récentes
                LocalDate.now().plusYears(1)   // et futures sur 1 an (modifiable)
        );
    }

    @Override
    public boolean estAbsent(Long medecinId, LocalDate date) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        return absenceMedecinRepository.existsByMedecinAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                medecin, date, date);
    }
}