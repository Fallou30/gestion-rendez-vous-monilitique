package com.sante.senegal.repositories;

import com.sante.senegal.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Méthodes de recherche personnalisées peuvent être ajoutées ici
    // Par exemple, pour trouver un patient par son nom ou son numéro de sécurité sociale
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByNumAssurance(String numAssurance);
    List<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

}
