package com.sante.senegal.repositories;

import com.sante.senegal.entities.PersonnelMedical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelMedicalRepository extends JpaRepository<PersonnelMedical, Long> {
}
