package com.sante.senegal.controllers;


import com.sante.senegal.dto.AuthResponseDto;
import com.sante.senegal.dto.LoginRequestDto;
import com.sante.senegal.exceptions.InscriptionException;
import com.sante.senegal.services.interfaces.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            AuthResponseDto response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException | InscriptionException e) {
            log.warn("Erreur d'authentification : {}", e.getMessage());
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors du login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(authService.getUserById(id));
        } catch (EntityNotFoundException e) {
            log.warn("Utilisateur non trouvé pour l'id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(creerReponseErreur("Utilisateur non trouvé"));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }
    @GetMapping("/public/verifier-email/{email}")
    public ResponseEntity<?> verifierDisponibiliteEmail(@PathVariable String email) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("disponible", !authService.emailExiste(email));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Une erreur inattendue s'est produite"));
        }
    }
    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> erreur = new HashMap<>();
        erreur.put("erreur", message);
        return erreur;
    }
}
