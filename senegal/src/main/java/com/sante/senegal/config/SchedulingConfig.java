package com.sante.senegal.config;

import com.sante.senegal.entities.RendezVous;
import com.sante.senegal.repositories.RendezVousRepository;
import com.sante.senegal.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {

    private final NotificationService notificationService;

    private final RendezVousRepository rendezVousRepository;

    /**
     * Rappel quotidien des rendez-vous du lendemain
     */
    @Scheduled(cron = "0 0 18 * * ?") // Tous les jours à 18h
    public void rappelRendezVousDemain() {
        LocalDate demain = LocalDate.now().plusDays(1);
        List<RendezVous> rendezVousDemain = rendezVousRepository
                .findRendezVousBetweenDates(
                        demain.atStartOfDay(),
                        demain.atTime(23, 59, 59)
                );

        for (RendezVous rdv : rendezVousDemain) {
            notificationService.notifierRappelRendezVous(rdv);
        }
    }

    /**
     * Nettoyage des fichiers temporaires
     */
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    public void nettoyageFichiersTemporaires() {
        // Logique de nettoyage des fichiers temporaires
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        try {
            Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().startsWith("medical_temp_"))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // Log l'erreur mais continue
                        }
                    });
        } catch (IOException e) {
            // Log l'erreur
        }
    }
}