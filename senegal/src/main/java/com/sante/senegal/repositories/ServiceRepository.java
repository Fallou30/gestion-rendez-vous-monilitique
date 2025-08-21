package com.sante.senegal.repositories;


import com.sante.senegal.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByStatut(Service.StatutService statut);

    List<Service> findByHopitalIdHopital(Long idHopital);

    List<Service> findByNomContainingIgnoreCase(String nom);

    List<Service> findByChefServiceId(Long idChefService);

    @Query("SELECT s FROM Service s WHERE s.hopital.idHopital = :idHopital AND s.statut = :statut")
    List<Service> findByHopitalAndStatut(@Param("idHopital") Long idHopital, @Param("statut") Service.StatutService statut);

    @Query("SELECT s FROM Service s WHERE s.capacitePatientsJour >= :capaciteMin")
    List<Service> findByCapaciteMinimum(@Param("capaciteMin") Integer capaciteMin);

    @Query("SELECT s FROM Service s WHERE s.hopital.ville = :ville AND s.statut = 'ACTIF'")
    List<Service> findActiveServicesByVille(@Param("ville") String ville);
}
