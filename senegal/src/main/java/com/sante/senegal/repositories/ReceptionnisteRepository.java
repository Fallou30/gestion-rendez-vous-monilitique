package com.sante.senegal.repositories;

import com.sante.senegal.entities.Receptionniste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionnisteRepository extends JpaRepository<Receptionniste, Long> {
    // Custom query methods can be defined here if needed
}
