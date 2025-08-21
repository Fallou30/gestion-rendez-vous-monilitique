package com.sante.senegal.repositories;


import com.sante.senegal.entities.Hopital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface HopitalRepository extends JpaRepository<Hopital, Long> {

    List<Hopital> findByStatut(Hopital.StatutHopital statut);

    List<Hopital> findByVille(String ville);

    List<Hopital> findByRegion(String region);

    List<Hopital> findByNomContainingIgnoreCase(String nom);

    List<Hopital> findByTypeEtablissement(String typeEtablissement);

    @Query("SELECT h FROM Hopital h WHERE h.capaciteLits >= :capaciteMin")
    List<Hopital> findByCapaciteMinimum(@Param("capaciteMin") Integer capaciteMin);

    @Query("SELECT h FROM Hopital h WHERE h.ville = :ville AND h.statut = :statut")
    List<Hopital> findByVilleAndStatut(@Param("ville") String ville, @Param("statut") Hopital.StatutHopital statut);

    @Query("SELECT h FROM Hopital h JOIN h.services s WHERE s.nom = :nomService")
    List<Hopital> findByServiceNom(@Param("nomService") String nomService);

    Optional <Hopital> findByNom(String nom);
}