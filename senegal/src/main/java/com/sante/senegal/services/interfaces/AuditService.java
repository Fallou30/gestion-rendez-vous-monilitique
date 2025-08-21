package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.AuditLogDto;
import java.time.LocalDate;
import java.util.List;

public interface AuditService {
    AuditLogDto enregistrerAction(AuditLogDto auditLogDto);
    List<AuditLogDto> obtenirHistoriqueAudit(String entite, Long entiteId);
    List<AuditLogDto> obtenirActionsUtilisateur(Long utilisateurId, LocalDate dateDebut, LocalDate dateFin);
}