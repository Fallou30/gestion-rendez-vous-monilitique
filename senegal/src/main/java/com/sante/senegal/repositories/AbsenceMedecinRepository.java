package com.sante.senegal.repositories;

import com.sante.senegal.entities.AbsenceMedecin;
import com.sante.senegal.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceMedecinRepository extends JpaRepository<AbsenceMedecin, Long> {

    List<AbsenceMedecin> findByMedecinAndDateFinAfterAndDateDebutBefore(
            Medecin medecin, LocalDate dateDebut, LocalDate dateFin);

    boolean existsByMedecinAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Medecin medecin, LocalDate date, LocalDate date2);
}
