package com.sante.senegal.repositories;

import com.sante.senegal.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntiteAndEntiteIdOrderByDateActionDesc(String entite, Long entiteId);

    List<AuditLog> findByActionAndDateActionBetween(String action,
                                                    LocalDateTime dateDebut, LocalDateTime dateFin);
    List<AuditLog> findByUtilisateurIdAndDateActionBetween(Long utilisateurId, LocalDateTime debut, LocalDateTime fin);
}