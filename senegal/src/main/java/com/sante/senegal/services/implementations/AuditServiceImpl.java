package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.AuditLogDto;
import com.sante.senegal.entities.AuditLog;
import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.repositories.AuditRepository;
import com.sante.senegal.repositories.UtilisateurRepository;
import com.sante.senegal.services.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditRepository auditRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ModelMapper modelMapper;

    @Override
    public AuditLogDto enregistrerAction(AuditLogDto auditLogDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(auditLogDto.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        AuditLog auditLog = modelMapper.map(auditLogDto, AuditLog.class);
        auditLog.setUtilisateur(utilisateur);
        auditLog.setDateAction(LocalDateTime.now());

        AuditLog savedAudit = auditRepository.save(auditLog);
        logger.info("Action d'audit enregistrée: {} sur {} (ID: {}) par {}",
                auditLogDto.getAction(), auditLogDto.getEntite(),
                auditLogDto.getEntiteId(), utilisateur.getEmail());

        return toDto(savedAudit);
    }

    @Override
    public List<AuditLogDto> obtenirHistoriqueAudit(String entite, Long entiteId) {
        return auditRepository.findByEntiteAndEntiteIdOrderByDateActionDesc(entite, entiteId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDto> obtenirActionsUtilisateur(Long utilisateurId, LocalDate dateDebut, LocalDate dateFin) {
        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = dateFin.atTime(23, 59, 59);

        return auditRepository.findByUtilisateurIdAndDateActionBetween(utilisateurId, debut, fin)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto toDto(AuditLog auditLog) {
        AuditLogDto dto = modelMapper.map(auditLog, AuditLogDto.class);
        dto.setUtilisateurId(auditLog.getUtilisateur().getId());
        dto.setUtilisateurEmail(auditLog.getUtilisateur().getEmail());
        dto.setUtilisateurNom(auditLog.getUtilisateur().getNom());
        dto.setUtilisateurPrenom(auditLog.getUtilisateur().getPrenom());
        return dto;
    }
}