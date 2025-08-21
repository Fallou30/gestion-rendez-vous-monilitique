package com.sante.senegal.repositories;

import com.sante.senegal.entities.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, Long> {

    Optional<JourFerie> findByDateAndSourceApi(LocalDate date, String sourceApi);

    boolean existsByDateAndAffecteDisponibilites(LocalDate date, Boolean affecteDisponibilites);

    List<JourFerie> findByDateBetween(LocalDate debut, LocalDate fin);

    List<JourFerie> findByDateBetweenAndAffecteDisponibilites(
            LocalDate debut, LocalDate fin, Boolean affecteDisponibilites);

    @Query("SELECT j FROM JourFerie j WHERE YEAR(j.date) = :annee")
    List<JourFerie> findByAnnee(@Param("annee") int annee);

    @Query("SELECT j FROM JourFerie j WHERE YEAR(j.date) = :annee AND MONTH(j.date) = :mois")
    List<JourFerie> findByAnneeAndMois(@Param("annee") int annee, @Param("mois") int mois);

    List<JourFerie> findByRegion(String region);

    List<JourFerie> findByType(JourFerie.TypeJourFerie type);

    @Query("SELECT j FROM JourFerie j WHERE j.date >= :dateDebut AND j.date <= :dateFin AND j.region = :region")
    List<JourFerie> findByPeriodeEtRegion(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("region") String region);

    @Modifying
    @Query("DELETE FROM JourFerie j WHERE j.sourceApi = :sourceApi AND YEAR(j.date) = :annee")
    void deleteBySourceApiAndAnnee(@Param("sourceApi") String sourceApi, @Param("annee") int annee);

    Optional<JourFerie> findByDateAndAffecteDisponibilites(LocalDate localDate, boolean b);
}