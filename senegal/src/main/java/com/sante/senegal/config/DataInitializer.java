package com.sante.senegal.config;

import com.sante.senegal.entities.*;
import com.sante.senegal.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final HopitalRepository hopitalRepository;
    private final ServiceRepository serviceRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() > 0) return;

        // 1. Créer et persister les hôpitaux
        Hopital hopital1 = creerEtPersisterHopital("Hôpital National de Dakar", "Av. Cheikh Anta Diop", "Dakar", "Dakar");
        Hopital hopital2 = creerEtPersisterHopital("Hôpital Serigne Eladji Malick Sy de Tivaone", "Route de Pire", "Pire", "Thies");

        // 2. Créer et persister le médecin
        Medecin medecin = creerEtPersisterMedecin("Fall", "Moussa", "moussa.fall@hopital.sn", "medecin123");

        // 3. Associer le médecin aux hôpitaux
        associerMedecinHopital(medecin, hopital1);
        associerMedecinHopital(medecin, hopital2);

        // 4. Créer et persister les services
        Service service1 = creerEtPersisterService("Service de Dermatologie", hopital1, medecin);
        Service service2 = creerEtPersisterService("Consultation Dermatologique", hopital2, medecin);

        // 5. Créer les disponibilités
        creerEtPersisterDisponibilites(medecin, service1, hopital1);
        creerEtPersisterDisponibilites(medecin, service2, hopital2);

        System.out.println("✅ Hôpitaux, médecin et services créés !");

        // 6. Patient
        Patient patient = Patient.builder()
                .nom("Sow")
                .prenom("Fatou")
                .email("patient@example.com")
                .motDePasse(passwordEncoder.encode("Password123"))
                .type(Utilisateur.TypeUtilisateur.PATIENT)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .sexe(Utilisateur.Sexe.FEMININ)
                .dateNaissance(LocalDate.of(1990, 6, 20))
                .lieuNaissance("Saint-Louis")
                .adresse("Parcelles Assainies, Dakar")
                .telephone("780000111")
                .numAssurance("ASSUR-999")
                .groupeSanguin("A+")
                .allergies("Pénicilline")
                .contactUrgenceNom("Mamadou Sow")
                .contactUrgenceTelephone("7811223344")
                .profession("Enseignante")
                .preferencesNotification("EMAIL")
                .build();
        utilisateurRepository.save(patient);

        // 7. Admin
        Utilisateur admin = Utilisateur.builder()
                .nom("Diallo")
                .prenom("Abdou")
                .email("admin@example.com")
                .motDePasse(passwordEncoder.encode("Admin123"))
                .type(Utilisateur.TypeUtilisateur.ADMIN)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .sexe(Utilisateur.Sexe.MASCULIN)
                .dateNaissance(LocalDate.of(1980, 1, 1))
                .lieuNaissance("Touba")
                .adresse("Point E, Dakar")
                .telephone("765432109")
                .build();
        utilisateurRepository.save(admin);

        // 8. Super Admin
        Utilisateur superAdmin = Utilisateur.builder()
                .nom("Ndoye")
                .prenom("Mariama")
                .email("superadmin@example.com")
                .motDePasse(passwordEncoder.encode("SuperAdmin123"))
                .type(Utilisateur.TypeUtilisateur.SUPER_ADMIN)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .sexe(Utilisateur.Sexe.FEMININ)
                .dateNaissance(LocalDate.of(1975, 12, 12))
                .lieuNaissance("Ziguinchor")
                .adresse("Almadies, Dakar")
                .telephone("771234567")
                .build();
        utilisateurRepository.save(superAdmin);

        System.out.println("✅ Utilisateurs initiaux injectés : Médecin, Patient, Admin, SuperAdmin.");
    }

    private Hopital creerEtPersisterHopital(String nom, String adresse, String ville, String region) {
        if (hopitalRepository.findByNom(nom).isEmpty()) {
            Hopital hopital = Hopital.builder()
                    .nom(nom)
                    .adresse(adresse)
                    .ville(ville)
                    .region(region)
                    .telephone("338800000")
                    .email("contact@" + nom.toLowerCase().replace(" ", "") + ".sn")
                    .typeEtablissement("Centre Hospitalier")
                    .statut(Hopital.StatutHopital.ACTIF)
                    .services(new ArrayList<>())
                    .medecins(new ArrayList<>())
                    .build();
            return hopitalRepository.save(hopital);
        }
        return hopitalRepository.findByNom(nom).get();
    }

    private Medecin creerEtPersisterMedecin(String nom, String prenom, String email, String password) {
        Medecin medecin = Medecin.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .motDePasse(passwordEncoder.encode(password))
                .telephone("770000001")
                .dateNaissance(LocalDate.of(1980, 2, 20))
                .sexe(Utilisateur.Sexe.MASCULIN)
                .type(Utilisateur.TypeUtilisateur.MEDECIN)
                .matricule("MED" + LocalDate.now().getYear() + "001")
                .specialite("Dermatologie")
                .numeroOrdre("ORDRE-12345")
                .titre("Dr.")
                .experience(10)
                .statut(Utilisateur.StatutUtilisateur.ACTIF)
                .hopitaux(new ArrayList<>())
                .build();
        return utilisateurRepository.save(medecin);
    }

    private void associerMedecinHopital(Medecin medecin, Hopital hopital) {
        medecin.getHopitaux().add(hopital);
        hopital.getMedecins().add(medecin);
        hopitalRepository.save(hopital);
        utilisateurRepository.save(medecin);
    }

    private Service creerEtPersisterService(String nom, Hopital hopital, Medecin chefService) {
        Service service = Service.builder()
                .nom(nom)
                .description("Service médical spécialisé")
                .hopital(hopital)
                .chefService(chefService)
                .statut(Service.StatutService.ACTIF)
                .build();

        Service savedService = serviceRepository.save(service);
        hopital.getServices().add(savedService);
        hopitalRepository.save(hopital);
        return savedService;
    }

    private void creerEtPersisterDisponibilites(Medecin medecin, Service service, Hopital hopital) {
        // Vérifier que le service est bien persisté
        if (service.getIdService() == null) {
            service = creerEtPersisterService(service.getNom(), hopital, medecin);
        }

        Disponibilite dispo1 = Disponibilite.builder()
                .medecin(medecin)
                .service(service)
                .hopital(hopital)
                .date(LocalDate.now())
                .jourSemaine(LocalDate.now().getDayOfWeek())
                .heureDebut(LocalTime.of(8, 0))
                .heureFin(LocalTime.of(12, 0))
                .statut(Disponibilite.StatutDisponibilite.DISPONIBLE)
                .build();

        Disponibilite dispo2 = Disponibilite.builder()
                .medecin(medecin)
                .service(service)
                .hopital(hopital)
                .date(LocalDate.now().plusDays(1))
                .jourSemaine(LocalDate.now().plusDays(1).getDayOfWeek())
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(13, 0))
                .statut(Disponibilite.StatutDisponibilite.DISPONIBLE)
                .build();

        disponibiliteRepository.saveAll(List.of(dispo1, dispo2));
    }
}