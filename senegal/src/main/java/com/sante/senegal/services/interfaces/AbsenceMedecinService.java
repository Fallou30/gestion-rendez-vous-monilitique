package com.sante.senegal.services.interfaces;

import com.sante.senegal.entities.AbsenceMedecin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour la gestion des absences des médecins
 */
public interface AbsenceMedecinService {

    /**
     * Crée une nouvelle absence pour un médecin
     * @param absence L'absence à créer
     * @return L'absence créée
     * @throws RuntimeException si le médecin n'est pas trouvé
     * @throws IllegalArgumentException si la date de début est postérieure à la date de fin
     */
    AbsenceMedecin creerAbsence(AbsenceMedecin absence);

    /**
     * Modifie une absence existante
     * @param id L'identifiant de l'absence à modifier
     * @param absenceMaj Les nouvelles données de l'absence
     * @return L'absence modifiée
     * @throws RuntimeException si l'absence n'est pas trouvée
     */
    AbsenceMedecin modifierAbsence(Long id, AbsenceMedecin absenceMaj);

    /**
     * Supprime une absence
     * @param id L'identifiant de l'absence à supprimer
     * @throws RuntimeException si l'absence n'est pas trouvée
     */
    void supprimerAbsence(Long id);

    /**
     * Récupère une absence par son identifiant
     * @param id L'identifiant de l'absence
     * @return L'absence trouvée ou Optional.empty()
     */
    Optional<AbsenceMedecin> getAbsenceById(Long id);

    /**
     * Récupère toutes les absences d'un médecin
     * @param medecinId L'identifiant du médecin
     * @return La liste des absences du médecin
     * @throws RuntimeException si le médecin n'est pas trouvé
     */
    List<AbsenceMedecin> getAbsencesByMedecin(Long medecinId);

    /**
     * Vérifie si un médecin est absent à une date donnée
     * @param medecinId L'identifiant du médecin
     * @param date La date à vérifier
     * @return true si le médecin est absent, false sinon
     * @throws RuntimeException si le médecin n'est pas trouvé
     */
    boolean estAbsent(Long medecinId, LocalDate date);
}