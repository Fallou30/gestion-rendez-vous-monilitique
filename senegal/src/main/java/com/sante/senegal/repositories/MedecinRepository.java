package com.sante.senegal.repositories;

import com.sante.senegal.entities.Medecin;
import com.sante.senegal.entities.Service;
import com.sante.senegal.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MedecinRepository extends JpaRepository<Medecin, Long> {
   Optional<Medecin> findByEmail(String email);
   List<Medecin> findByStatut(Utilisateur.StatutUtilisateur statutUtilisateur);
   Optional<Medecin> findByNumeroOrdre(String numeroOrdre);
   List<Medecin> findBySpecialiteContainingIgnoreCase(String specialite);
   List<Medecin> findByService(Service service);
   // Find doctors by service ID
   @Query("SELECT m FROM Medecin m WHERE m.service.idService = :serviceId")
   List<Medecin> findByServiceId(@Param("serviceId") Long serviceId);

   // Find doctors by hospital ID
   @Query("SELECT m FROM Medecin m JOIN m.hopitaux h WHERE h.idHopital = :hopitalId")
   List<Medecin> findByHopitalId(@Param("hopitalId") Long hopitalId);

   // Find doctors by service and hospital
   @Query("SELECT DISTINCT m FROM Medecin m JOIN m.hopitaux h " +
           "WHERE m.service.idService = :serviceId AND h.idHopital = :hopitalId")
   List<Medecin> findByServiceAndHopital(@Param("serviceId") Long serviceId,
                                         @Param("hopitalId") Long hopitalId);
   Medecin findById(long id);
   List<Medecin> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String terme, String terme1);
}