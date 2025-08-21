package com.sante.senegal.services.interfaces;

import com.sante.senegal.entities.Utilisateur;

public interface EmailService {
    void envoyerEmailBienvenue(Utilisateur utilisateur);
    void notifierNouvelleDemandeInscription(Utilisateur utilisateur);
    void envoyerAccesInitiaux(Utilisateur utilisateur, String motDePasseTemp);
    void envoyerNotificationValidation(Utilisateur utilisateur, boolean approuve, String commentaire);
    void envoyerNotificationModification(Utilisateur utilisateur, String sujet, String messageDetail);
    void envoyerNotificationSuppression(Utilisateur utilisateur, String sujet, String message);
    void envoyerNotificationRendezVous(String email, String sujet, String texte);
}