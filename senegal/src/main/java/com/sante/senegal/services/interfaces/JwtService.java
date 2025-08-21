package com.sante.senegal.services.interfaces;

import com.sante.senegal.entities.Utilisateur;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String generateToken(Utilisateur utilisateur);
    boolean isTokenValid(String token, UserDetails userDetails);
    String extractUsername(String token);
    Date extractExpiration(String token);
}