package com.sante.senegal.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CalendrierHealthCheck {

    private final DateNagerCalendrierService calendrierService;

    public CalendrierHealthCheck(DateNagerCalendrierService calendrierService) {
        this.calendrierService = calendrierService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkApiConnectivity() {
        log.info("Vérification de la connectivité avec Date Nager API...");

        boolean isConnected = calendrierService.testerConnectivite();

        if (isConnected) {
            log.info("✅ Connectivité Date Nager API: OK");
        } else {
            log.warn("❌ Connectivité Date Nager API: ÉCHEC - Utilisation des données par défaut");
        }
    }
}