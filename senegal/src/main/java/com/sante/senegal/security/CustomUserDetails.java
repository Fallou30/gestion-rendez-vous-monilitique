package com.sante.senegal.security;

import com.sante.senegal.entities.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public record CustomUserDetails(Utilisateur utilisateur) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getType().name())); // Ajout du préfixe ROLE_
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return utilisateur.getStatut() == Utilisateur.StatutUtilisateur.ACTIF;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.getStatut() == Utilisateur.StatutUtilisateur.ACTIF;
    }
}
