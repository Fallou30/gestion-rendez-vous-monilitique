package com.sante.senegal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDto {
    private Long id;
    private String action;
    private String entite;
    private Long entiteId;
    private Long utilisateurId;
    private String utilisateurEmail;
    private String utilisateurNom; // Ajouté pour affichage
    private String utilisateurPrenom; // Ajouté pour affichage
    private String details;
    private LocalDateTime dateAction;
}