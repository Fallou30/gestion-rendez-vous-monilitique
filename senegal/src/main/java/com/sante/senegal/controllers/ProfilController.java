package com.sante.senegal.controllers;

import com.sante.senegal.dto.*;
import com.sante.senegal.services.interfaces.ProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profil")
@RequiredArgsConstructor
public class ProfilController {

    private final ProfilService profilService;

    @GetMapping("/{userId}")
    public ResponseEntity<ProfilUtilisateurDto> getProfil(@PathVariable Long userId) {
        return ResponseEntity.ok(profilService.consulterProfil(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfilUtilisateurDto> updateProfil(
            @PathVariable Long userId,
            @RequestBody ProfilUtilisateurDto dto) {
        return ResponseEntity.ok(profilService.modifierProfil(userId, dto));
    }

    @PostMapping("/{userId}/changer-mot-de-passe")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangementMotDePasseDto dto) {
        profilService.changerMotDePasse(userId, dto);
        return ResponseEntity.ok().build();
    }
}