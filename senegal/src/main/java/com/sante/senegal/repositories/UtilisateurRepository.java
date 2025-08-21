package com.sante.senegal.repositories;

import com.sante.senegal.dto.UtilisateurDto;
import com.sante.senegal.entities.Utilisateur;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    List<Utilisateur> findByTypeAndStatut(Utilisateur.TypeUtilisateur type, Utilisateur.StatutUtilisateur statut);
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    Utilisateur findByType(Utilisateur.TypeUtilisateur type);

    List<Utilisateur> findAll(Specification<Utilisateur> spec, Sort dateCreation);
}