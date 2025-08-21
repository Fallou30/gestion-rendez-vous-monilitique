package com.sante.senegal.services.implementations;

import com.sante.senegal.entities.Utilisateur;
import com.sante.senegal.services.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void envoyerEmailBienvenue(Utilisateur utilisateur) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Bienvenue sur notre plateforme");
        message.setText("Bonjour " + utilisateur.getPrenom() + ",\n\n"
                + "Votre compte a été créé avec succès.\n"
                + "Email: " + utilisateur.getEmail() + "\n\n"
                + "Cordialement,\nL'équipe SanteSenegal");
        mailSender.send(message);
    }

    @Override
    public void notifierNouvelleDemandeInscription(Utilisateur utilisateur) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("admin@santesenegal.com");
            helper.setSubject("Nouvelle demande d'inscription - Action Requise");

            String frontendUrl = "https://votre-application.angular.app";
            String validationUrl = frontendUrl + "/admin/validation-demandes?userId=" + utilisateur.getId();

            String htmlContent = String.format(
                    "<h3>Nouvelle demande d'inscription</h3>" +
                            "<p><strong>Nom complet:</strong> %s %s</p>" +
                            "<p><strong>Email:</strong> %s</p>" +
                            "<p><strong>Type:</strong> %s</p>" +
                            "<br/>" +
                            "<p>Veuillez traiter cette demande :</p>" +
                            "<div style='margin: 20px 0;'>" +
                            "   <a href='%s&action=approve' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; margin-right: 10px; border-radius: 5px;'>Approuver</a>" +
                            "   <a href='%s&action=reject' style='background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Rejeter</a>" +
                            "</div>" +
                            "<p>Ou accédez à <a href='%s/admin/dashboard'>l'interface d'administration</a></p>",
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getType(),
                    validationUrl,
                    validationUrl,
                    frontendUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email HTML", e);
            notifierNouvelleDemandeInscriptionFallback(utilisateur);
        }
    }

    @Override
    public void envoyerAccesInitiaux(Utilisateur utilisateur, String motDePasseTemp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Vos accès initiaux");
        message.setText("Bonjour " + utilisateur.getPrenom() + ",\n\n"
                + "Votre compte a été créé par un administrateur.\n"
                + "Email: " + utilisateur.getEmail() + "\n"
                + "Mot de passe temporaire: " + motDePasseTemp + "\n\n"
                + "Nous vous recommandons de changer ce mot de passe après votre première connexion.\n\n"
                + "Cordialement,\nL'équipe SanteSenegal");
        mailSender.send(message);
    }

    @Override
    public void envoyerNotificationValidation(Utilisateur utilisateur, boolean approuve, String commentaire) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Résultat de votre demande d'inscription");

        String texte = "Bonjour " + utilisateur.getPrenom() + ",\n\n";
        if (approuve) {
            texte += """
                    Votre demande d'inscription a été approuvée.
                    Vous pouvez maintenant vous connecter à votre compte.
                    Vos informations de connexion restent inchangé!
                    """;
        } else {
            texte += "Votre demande d'inscription a été rejetée.\n"
                    + "Raison: " + (commentaire != null ? commentaire : "Non spécifiée") + "\n";
        }
        texte += "\nCordialement,\nL'équipe SanteSenegal";

        message.setText(texte);
        mailSender.send(message);
    }

    @Override
    public void envoyerNotificationModification(Utilisateur utilisateur, String sujet, String messageDetail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(utilisateur.getEmail());
            helper.setSubject(sujet);

            String htmlContent = String.format(
                    "<h3>Notification de modification</h3>" +
                            "<p>Bonjour %s %s,</p>" +
                            "<p>%s</p>" +
                            "<p>Si vous n'avez pas initié ces changements, veuillez contacter immédiatement le support.</p>" +
                            "<br/>" +
                            "<p>Cordialement,<br/>L'équipe SanteSenegal</p>",
                    utilisateur.getPrenom(),
                    utilisateur.getNom(),
                    messageDetail.replace("\n", "<br/>")
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification de modification", e);
            envoyerNotificationModificationFallback(utilisateur, sujet, messageDetail);
        }
    }

    @Override
    public void envoyerNotificationSuppression(Utilisateur utilisateur, String sujet, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(utilisateur.getEmail());
            helper.setSubject(sujet);

            String htmlContent = String.format(
                    "<h3>Notification de désactivation de compte</h3>" +
                            "<p>Bonjour %s %s,</p>" +
                            "<p>%s</p>" +
                            "<p>Pour toute question, veuillez contacter notre support.</p>" +
                            "<br/>" +
                            "<p>Cordialement,<br/>L'équipe SanteSenegal</p>",
                    utilisateur.getPrenom(),
                    utilisateur.getNom(),
                    message
            );

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification de suppression", e);
            envoyerNotificationSuppressionFallback(utilisateur, sujet, message);
        }
    }

    @Override
    public void envoyerNotificationRendezVous(String email, String sujet, String texte) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(sujet);
        message.setText(texte);
        mailSender.send(message);
    }

    private void notifierNouvelleDemandeInscriptionFallback(Utilisateur utilisateur) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("admin@santesenegal.com");
        message.setSubject("Nouvelle demande d'inscription");

        String frontendUrl = "https://loclhost:4200";
        String adminUrl = frontendUrl + "/admin/dashboard";

        message.setText(
                "Une nouvelle demande d'inscription a été soumise par:\n\n" +
                        "Nom: " + utilisateur.getNom() + " " + utilisateur.getPrenom() + "\n" +
                        "Email: " + utilisateur.getEmail() + "\n" +
                        "Type: " + utilisateur.getType() + "\n\n" +
                        "Veuillez la traiter dans l'interface d'administration:\n" +
                        adminUrl
        );

        mailSender.send(message);
    }

    private void envoyerNotificationModificationFallback(Utilisateur utilisateur, String sujet, String messageDetail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject(sujet);
        message.setText(
                "Bonjour " + utilisateur.getPrenom() + " " + utilisateur.getNom() + ",\n\n" +
                        messageDetail + "\n\n" +
                        "Si vous n'avez pas initié ces changements, veuillez contacter immédiatement le support.\n\n" +
                        "Cordialement,\nL'équipe SanteSenegal"
        );
        mailSender.send(message);
    }

    private void envoyerNotificationSuppressionFallback(Utilisateur utilisateur, String sujet, String message) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(utilisateur.getEmail());
        simpleMessage.setSubject(sujet);
        simpleMessage.setText(
                "Bonjour " + utilisateur.getPrenom() + " " + utilisateur.getNom() + ",\n\n" +
                        message + "\n\n" +
                        "Pour toute question, veuillez contacter notre support.\n\n" +
                        "Cordialement,\nL'équipe SanteSenegal"
        );
        mailSender.send(simpleMessage);
    }
}