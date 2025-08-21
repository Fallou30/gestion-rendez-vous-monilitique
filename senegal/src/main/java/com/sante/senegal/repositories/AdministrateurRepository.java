package com.sante.senegal.repositories;

import com.sante.senegal.entities.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}
