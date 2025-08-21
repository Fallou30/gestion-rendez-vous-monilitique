import { Component, OnInit } from '@angular/core';
import { ConsultationService } from '../../../core/services/consultation.service';
import { DashboardData, DashboardService } from '../../../core/services/dashboard.service';
import { ExamenService } from '../../../core/services/examen.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { CommonModule, formatDate } from '@angular/common';
import { DashboardNavComponent } from '../../../core/components/dashboard-nav/dashboard-nav.component';
import { TypeUtilisateur } from '../../../core/models/utilisateur/utilisateur.module';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { DossierMedicalService } from '../../../core/services/dossier-medical.service';
import { catchError } from 'rxjs/operators';


@Component({
  selector: 'app-medecin-dashboard',
  templateUrl: './medecien-dashboard.component.html',
  styleUrls: ['./medecien-dashboard.component.scss'],
  imports: [CommonModule,DashboardNavComponent],
  standalone:true,
})
export class MedecinDashboardComponent implements OnInit {
  userType=TypeUtilisateur.MEDECIN
  // Données du dashboard
  dashboardData: DashboardData | null = null;
  medecinId: number =1; // À récupérer depuis l'authentification
  dateActuelle: string = '';
  userNomComplet: string = '';
  // États de chargement
  isLoading = true;
  error: string | null = null;
  
  // Données pour les graphiques
  statsConsultations: any = null;
  rendezVousParStatut: any[] = [];
  
  // Rendez-vous du jour
  rendezVousDuJour: any[] = [];
  prochainRendezVous: any | null = null;
  
  // Examens
  examensEnAttente: any[] = [];
  examensUrgents: any[] = [];
  
  // Statistiques rapides
  nombreConsultationsJour = 0;
  tempsAttenteMoyen = 0;
  tauxSatisfaction = 0;
  
  // Onglet actif pour le suivi patient
  activeTab = 'consultations';
  
  // Patient sélectionné pour le suivi
  patientSelectionne: any = null;
  consultationsPatient: any[] = [];
  examensPatient: any[] = [];
  prescriptionsPatient: any[] = [];

  constructor(
    private dashboardService: DashboardService,
    private rendezVousService: RendezVousService,
    private consultationService: ConsultationService,
    private examenService: ExamenService,
    private authService: AuthService,
    private dossierMedicalService: DossierMedicalService,
    private router: Router,
  ) {}

  ngOnInit() {
    const user = this.authService.currentUserValue;
  if (user && user.id !== undefined) {
    this.userNomComplet = `Dr. ${user.prenom} ${user.nom}`;
    this.medecinId = user.id;
  }
    this.loadDashboardData();
    this.loadAdditionalStats();
    this.dateActuelle = formatDate(new Date(), 'fullDate', 'fr-FR'); // ou 'shortDate' selon le format voulu
  }

  loadDashboardData() {
    this.isLoading = true;
    this.dashboardService.getDashboardData(this.medecinId).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.rendezVousDuJour = data.rendezVousDuJour;
        this.examensEnAttente = data.examensEnAttente;
        this.examensUrgents = data.examensUrgents;
        this.statsConsultations = data.statistiques;

        
        // Calculer le prochain rendez-vous
        this.calculateProchainRendezVous();
        
        // Calculer les stats par statut
        this.calculateRendezVousParStatut();
        
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des données';
        this.isLoading = false;
        console.error('Erreur dashboard:', error);
      }
    });
  }

  loadAdditionalStats() {
 
    this.dashboardService.getStatsGlobales(this.medecinId).subscribe({
    next: (stats) => {
      this.nombreConsultationsJour = stats.consultationsJour;
      this.tempsAttenteMoyen = stats.dureeMoyenne;
      this.tauxSatisfaction = stats.tauxSatisfaction;
    },
    error: () => {
      this.nombreConsultationsJour = 0;
      this.tempsAttenteMoyen = 0;
      this.tauxSatisfaction = 0;
    }
  });

  }

  calculateProchainRendezVous() {
    if (this.rendezVousDuJour.length > 0) {
      const now = new Date();
      const prochains = this.rendezVousDuJour
        .filter(rdv => new Date(rdv.dateHeureDebut) > now)
        .sort((a, b) => new Date(a.dateHeureDebut).getTime() - new Date(b.dateHeureDebut).getTime());
      
      this.prochainRendezVous = prochains.length > 0 ? prochains[0] : null;
    }
  }

  calculateRendezVousParStatut() {
    const statuts = ['PROGRAMME', 'CONFIRME', 'EN_COURS', 'TERMINE', 'ANNULE'];
    this.rendezVousParStatut = statuts.map(statut => ({
      statut,
      nombre: this.rendezVousDuJour.filter(rdv => rdv.statut === statut).length
    }));
  }

  // Actions sur les rendez-vous
  confirmerRendezVous(id: number) {
    this.rendezVousService.confirmerRendezVous(id).subscribe({
      next: () => {
        this.loadDashboardData();
        this.showNotification('Rendez-vous confirmé avec succès', 'success');
      },
      error: (error) => {
        this.showNotification('Erreur lors de la confirmation', 'error');
        console.error('Erreur confirmation:', error);
      }
    });
  }

  commencerConsultation(rdv: any) {
    this.rendezVousService.commencerConsultation(rdv.id).subscribe({
      next: () => {
        // Vérifier/créer le dossier patient puis naviguer
        this.verifierOuCreerDossierPatient(rdv.patient.id).subscribe({
          next: () => {
            this.naviguerVersEspacePatient(rdv.patient.id, rdv.id);
          }
        });
      }
    });
  }

  terminerConsultation(id: number) {
    this.rendezVousService.terminerConsultation(id).subscribe({
      next: () => {
        this.loadDashboardData();
        this.showNotification('Consultation terminée', 'success');
      },
      error: (error) => {
        this.showNotification('Erreur lors de la fin de consultation', 'error');
        console.error('Erreur fin consultation:', error);
      }
    });
  }

  // Gestion du suivi patient
  ouvrirSuiviPatient(patient: any) {
    this.patientSelectionne = patient;
    this.loadPatientData(patient.id);
  }
  // À ajouter dans le component  POUR NAVIGUER VERS L'ESPACE PATIENT POUR AFFICHER LES CONSULTATIONS ET EXAMENS
  // Cette méthode est appelée depuis le template pour naviguer vers l'espace patient

  naviguerVersEspacePatient(patientId: number, rendezVousId?: number) {
    this.router.navigate(['/medecin/patient', patientId], {
      queryParams: { rendezVousId: rendezVousId }
    });
  }
 verifierOuCreerDossierPatient(patientId: number) {
  return this.dossierMedicalService.getDossierPatient(patientId)
    .pipe(
      catchError((error: any) => {
        if (error.status === 404) {
          // Solution 1: Si creerDossier attend un CreateDossierRequest
          this.dossierMedicalService.creerDossier({
            patientId: patientId,
            antecedentsMedicaux: '',
            antecedentsFamiliaux: '',
            vaccinations: ''
          }).subscribe({
            next: (dossier) => {
              this.showNotification('Dossier médical créé avec succès', 'success');
              return dossier;
            },
            error: (error) => {
              this.showNotification('Erreur lors de la création du dossier médical', 'error');
              console.error('Erreur création dossier:', error);
            }
          });         
        
        }
        return throwError(() => error);
      })
    );
}
  loadPatientData(patientId: number) {
    // Charger les consultations du patient
    this.consultationService.getHistoriqueConsultations(patientId).subscribe({
      next: (consultations) => {
        this.consultationsPatient = consultations;
      },
      error: (error) => {
        console.error('Erreur chargement consultations:', error);
      }
    });

    // Charger les examens du patient
    this.examenService.getExamensPatient(patientId).subscribe({
      next: (examens) => {
        this.examensPatient = examens;
      },
      error: (error) => {
        console.error('Erreur chargement examens:', error);
      }
    });
  }

  fermerSuiviPatient() {
    this.patientSelectionne = null;
    this.consultationsPatient = [];
    this.examensPatient = [];
    this.prescriptionsPatient = [];
  }

  // Utilitaires
  getStatutClass(statut: string): string {
    const classes: { [key: string]: string } = {
      'PROGRAMME': 'statut-programme',
      'CONFIRME': 'statut-confirme',
      'EN_COURS': 'statut-en-cours',
      'TERMINE': 'statut-termine',
      'ANNULE': 'statut-annule',
      'URGENT': 'statut-urgent'
    };
    return classes[statut] || 'statut-default';
  }

  getUrgenceClass(urgence: string): string {
    const classes: { [key: string]: string } = {
      'FAIBLE': 'urgence-faible',
      'MOYENNE': 'urgence-moyenne',
      'ELEVEE': 'urgence-elevee',
      'CRITIQUE': 'urgence-critique'
    };
    return classes[urgence] || 'urgence-normale';
  }

  formatTime(date: string): string {
    return new Date(date).toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR');
  }

  // Notifications
  private showNotification(message: string, type: 'success' | 'error' | 'info') {
    // Implémentation des notifications (toast, snackbar, etc.)
    console.log(`${type.toUpperCase()}: ${message}`);
  }

  // Méthodes pour les actions rapides
  refreshDashboard() {
    this.loadDashboardData();
  }

  exporterDonnees() {
    // Implémentation de l'export
    console.log('Export des données du dashboard');
  }

  voirCalendrierComplet(): void {
    this.router.navigate(['/medecin/calendrier']);
  }
}

function throwError(error: any): any {
  throw new Error('Function not implemented.');
}
