// patient-dashboard.component.ts
import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Observable, forkJoin } from 'rxjs';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import {
  Consultation,
  ConsultationService,
} from '../../../core/services/consultation.service';
import { Examen, ExamenService } from '../../../core/services/examen.service';
import {
  Prescription,
  PrescriptionService,
} from '../../../core/services/prescription.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TypeUtilisateur } from '../../../core/models/utilisateur/utilisateur.module';
import { DashboardNavComponent } from '../../../core/components/dashboard-nav/dashboard-nav.component';

interface DashboardStats {
  prochainRdv: number;
  consultationsTerminees: number;
  examensEnAttente: number;
  prescriptionsActives: number;
}

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DashboardNavComponent],
  templateUrl: './patient-dashboard.component.html',
  styleUrls: ['./patient-dashboard.component.scss'],
})
export class PatientDashboardComponent implements OnInit {
  // Signals pour Angular 20
  userType= TypeUtilisateur.PATIENT;
  activeTab = signal<string>('rendez-vous');
  patientName = signal<string>('Jean Dupont');
  patientId = signal<number>(1);
  notifications = signal<any[]>([
    {
      id: 1,
      type: 'rdv',
      message: 'Rendez-vous demain à 14h30 avec Dr. Martin',
      time: '2h',
    },
    {
      id: 2,
      type: 'prescription',
      message: 'Prescription expire dans 3 jours',
      time: '1j',
    },
    {
      id: 3,
      type: 'examen',
      message: "Résultats d'examen disponibles",
      time: '30min',
    },
  ]);

  showNotifications = signal<boolean>(false); // ✅

  searchTerm = signal<string>('');
  examenFilter = signal<string>('tous');

  // Data signals
  rendezVousList = signal<any[]>([]);
  consultationsList = signal<Consultation[]>([]);
  examensList = signal<Examen[]>([]);
  prescriptionsList = signal<Prescription[]>([]);

  // Computed signals
  prochainRdv = computed(() => {
    const rdvs = this.rendezVousList().filter(
      (rdv) => rdv.statut === 'CONFIRME' && new Date(rdv.dateHeure) > new Date()
    );
    return rdvs.length;
  });

  consultationsTerminees = computed(() => {
    return this.consultationsList().filter((c) => c.statut === 'TERMINEE')
      .length;
  });

  examensEnAttente = computed(() => {
    return this.examensList().filter((e) => e.statut === 'EN_ATTENTE').length;
  });
  redirigerVersFormulaireRdv(): void {
  this.router.navigate(['/prendre/rendez-vous']);
}

  prescriptionsActives = computed(() => {
    return this.prescriptionsList().filter((p) => p.statut === 'ACTIVE').length;
  });
  toggleNotifications() {
    this.showNotifications.update((v) => !v); // ✅ inverse la valeur
  }
  filteredConsultations = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.consultationsList().filter(
      (c) =>
        c.symptomes.toLowerCase().includes(term) ||
        c.diagnostic.toLowerCase().includes(term) ||
        c.recommandations.toLowerCase().includes(term)
    );
  });

  filteredExamens = computed(() => {
    const filter = this.examenFilter();
    let examens = this.examensList();

    if (filter === 'en-attente') {
      examens = examens.filter((e) => e.statut === 'EN_ATTENTE');
    } else if (filter === 'terminés') {
      examens = examens.filter((e) => e.statut === 'TERMINE');
    }

    return examens;
  });

  constructor(
    private rendezVousService: RendezVousService,
    private consultationService: ConsultationService,
    private examenService: ExamenService,
    private prescriptionService: PrescriptionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadDashboardData();
     this.loadUserData();
  }

  loadDashboardData() {
    const patientId = this.patientId();
 
    // Charger les données en parallèle
    forkJoin({
      rendezVous:
        this.rendezVousService.getUpcomingRendezVousByPatient(patientId),
      consultations:
        this.consultationService.getHistoriqueConsultations(patientId),
      examens: this.examenService.getExamensPatient(patientId),
      prescriptions:
        this.prescriptionService.getPrescriptionsActives(patientId),
    }).subscribe({
      next: (data) => {
        this.rendezVousList.set(data.rendezVous);
        this.consultationsList.set(data.consultations);
        this.examensList.set(data.examens);
        this.prescriptionsList.set(data.prescriptions);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des données:', error);
      },
    });
  }
   loadUserData() {
     const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.patientName.set( `${currentUser.prenom} ${currentUser.nom}`);
      this.patientId.set(currentUser.id!);
    } else {
      // Rediriger vers la page de connexion si aucun utilisateur n'est connecté
      this.router.navigate(['/login']);
    }
  }

  setActiveTab(tab: string) {
    this.activeTab.set(tab);
  }

  setExamenFilter(filter: string) {
    this.examenFilter.set(filter);
  }

  filterConsultations() {
    // La logique de filtrage est gérée par le computed signal
  }

  // Méthodes pour les actions
  openNewRdvModal() {
     console.log('Ouverture du modal pour nouveau RDV');
    // Ouvrir un modal pour créer un nouveau rendez-vous
    this.router.navigate(['/prendre-rendez-vous']);
   
  }

  viewRdv(id: number) {
    // Voir les détails d'un rendez-vous
    console.log('Voir RDV:', id);
  }

  navigateToAppointment() {
    this.router.navigate(['/prendre-rendez-vous']);
  }

  confirmerRdv(id: number) {
    this.rendezVousService.confirmerRendezVous(id).subscribe({
      next: (rdv) => {
        console.log('RDV confirmé:', rdv);
        this.loadDashboardData();
      },
      error: (error) => {
        console.error('Erreur lors de la confirmation:', error);
      },
    });
  }

  annulerRdv(id: number) {
    this.rendezVousService.annulerRendezVous(id).subscribe({
      next: (rdv) => {
        console.log('RDV annulé:', rdv);
        this.loadDashboardData();
      },
      error: (error) => {
        console.error("Erreur lors de l'annulation:", error);
      },
    });
  }

  // Méthodes utilitaires
  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  formatTime(date: Date): string {
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getStatutClass(statut: string): string {
    return statut.toLowerCase().replace('_', '-');
  }

  getExamenStatusClass(statut: string): string {
    return statut.toLowerCase().replace('_', '-');
  }

  getPrescriptionStatusClass(statut: string): string {
    return statut.toLowerCase().replace('_', '-');
  }

  getMedicamentStatusClass(statut: string): string {
    return statut.toLowerCase().replace('_', '-');
  }
}
