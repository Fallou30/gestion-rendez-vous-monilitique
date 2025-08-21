import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';
import { PlanningService } from '../../../core/services/planning.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { ExportService } from '../../../core/services/export.service';
import { ServiceService } from '../../../core/services/service.service';
import { HopitalService } from '../../../core/services/hopital.service';
import { TypeUtilisateur, StatutUtilisateur } from '../../../core/models/utilisateur/utilisateur.module';
import { Chart, registerables } from 'chart.js';
import { DatePipe, CommonModule, CurrencyPipe } from '@angular/common';
import { StatistiqueService } from '../../../core/services/statistiques.service';
import { ConsultationService } from '../../../core/services/consultation.service';
import { forkJoin, map, Observable } from 'rxjs';
import { DashboardNavComponent } from '../../../core/components/dashboard-nav/dashboard-nav.component';
import { RouterLink, RouterModule } from '@angular/router';

interface RendezVousParMois {
  month: string;
  count: number;
}

interface MedicamentPrescritStat {
  nomMedicament: string;
  nombrePrescriptions: number;
}

interface ConsultationParSpecialite {
  specialite: string;
  count: number;
}

interface SystemStats {
  totalPatients: number;
  totalMedecins: number;
  totalRendezVous: number;
  rdvConfirmes: number;
  rdvEnAttente: number;
  rdvAnnules: number;
  revenusMensuel: number;
  creneauxDisponibles: number;
  creneauxReserves: number;
  totalReceptionnistes: number;
  pendingValidations: number;
  rendezVousEnCours: number;
  totalHopitaux: number;
  totalServices: number;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
  standalone: true,
  imports: [CommonModule, DashboardNavComponent, CurrencyPipe, RouterLink],
  providers: [DatePipe] // DatePipe est bien fourni ici
})
export class AdminDashboardComponent implements OnInit {
  userType = TypeUtilisateur.ADMIN;
  today = new Date();
  startOfMonth: string;
  endOfMonth: string;

  // Statistiques principales
  systemStats: SystemStats = {
    totalPatients: 0,
    totalMedecins: 0,
    totalRendezVous: 0,
    rdvConfirmes: 0,
    rdvEnAttente: 0,
    rdvAnnules: 0,
    revenusMensuel: 0,
    creneauxDisponibles: 0,
    creneauxReserves: 0,
    totalReceptionnistes: 0,
    pendingValidations: 0,
    rendezVousEnCours: 0,
    totalHopitaux: 0,
    totalServices: 0
  };

  // Données pour les graphiques
  chartData: {
    rendezVousParMois: RendezVousParMois[],
    medicamentsPrescrits: MedicamentPrescritStat[],
    consultationsParSpecialite: ConsultationParSpecialite[]
  } = {
    rendezVousParMois: [],
    medicamentsPrescrits: [],
    consultationsParSpecialite: []
  };

  // Liste des rendez-vous du jour et autres listes
  rendezVousDuJour: any[] = [];
  rendezVousUrgents: any[] = [];
  demandesValidation: any[] = [];
  creneauxDisponibles: any[] = [];

  // Data for HTML-specific lists (mocked or populated from services)
  pendingValidations: any[] = [];
  recentActivities: any[] = [];
  upcomingAppointments: any[] = [];

  // Charts
  rdvChart: Chart | null = null;
  medicamentChart: Chart | null = null;
  specialiteChart: Chart | null = null;
  weeklyChart: Chart | null = null;

  constructor(
    private adminService: AdminService,
    private planningService: PlanningService,
    private rendezVousService: RendezVousService,
    private statistiqueService: StatistiqueService,
    private exportService: ExportService,
    private consultationService: ConsultationService,
    private serviceService: ServiceService,
    private hopitalService: HopitalService,
    private datePipe: DatePipe // DatePipe est injecté
  ) {
    Chart.register(...registerables);

    // --- CORRECTION: Ajout des composants d'heure aux dates ---
    // Pour le début du mois, fixez l'heure à 00:00:00
    this.startOfMonth = this.datePipe.transform(
      new Date(this.today.getFullYear(), this.today.getMonth(), 1),
      'yyyy-MM-ddTHH:mm:ss' // Format ISO 8601 complet pour LocalDateTime
    )!;

    // Pour la fin du mois, fixez l'heure à 23:59:59:999 pour couvrir toute la journée
    const lastDayOfMonth = new Date(this.today.getFullYear(), this.today.getMonth() + 1, 0);
    lastDayOfMonth.setHours(23, 59, 59, 999); // Définir à la fin de la dernière seconde du jour
    this.endOfMonth = this.datePipe.transform(
      lastDayOfMonth,
      'yyyy-MM-ddTHH:mm:ss' // Format ISO 8601 complet pour LocalDateTime
    )!;
    // --- FIN DE LA CORRECTION ---
  }

  ngOnInit(): void {
    this.loadAllData();
    this.loadMockData();
  }

  loadAllData(): void {
    this.loadStats();
    this.loadRendezVous();
    this.loadDemandesValidation();
    this.loadCreneauxDisponibles();
    this.loadChartData();
  }

  loadStats(): void {
    forkJoin([
      this.adminService.listerUtilisateurs(TypeUtilisateur.PATIENT),
      this.adminService.listerUtilisateurs(TypeUtilisateur.MEDECIN),
      this.adminService.listerUtilisateurs(TypeUtilisateur.RECEPTIONNISTE),
      this.rendezVousService.getRendezVousBetweenDates(this.startOfMonth, this.endOfMonth),
      // --- CORRECTION: Assurez-vous que les créneaux disponibles utilisent aussi les bonnes dates ---
      this.planningService.getCreneauxDisponibles(this.startOfMonth, this.endOfMonth),
      // --- FIN DE LA CORRECTION ---
      this.adminService.listerDemandesValidation(),
      this.hopitalService.getAllHopitaux(),
      this.serviceService.getAllServices()
    ]).subscribe(([patients, medecins, receptionnistes, rdvs, creneaux, demandes, hopitaux, services]) => {
      this.systemStats.totalPatients = patients.length;
      this.systemStats.totalMedecins = medecins.length;
      this.systemStats.totalReceptionnistes = receptionnistes.length;
      this.systemStats.pendingValidations = demandes.length;
      this.systemStats.totalHopitaux = hopitaux.length;
      this.systemStats.totalServices = services.length;

      this.systemStats.totalRendezVous = rdvs.length;
      this.systemStats.rdvConfirmes = rdvs.filter((r: any) => r.statut === 'CONFIRME').length;
      this.systemStats.rdvEnAttente = rdvs.filter((r: any) => r.statut === 'EN_ATTENTE').length;
      this.systemStats.rdvAnnules = rdvs.filter((r: any) => r.statut === 'ANNULE').length;

      const todayString = this.datePipe.transform(this.today, 'yyyy-MM-dd'); // Cette variable est pour la comparaison, pas pour l'API
      this.rendezVousService.getRendezVousDuJour().subscribe(rdvsToday => {
        this.systemStats.rendezVousEnCours = rdvsToday.filter((r: any) => r.statut === 'EN_COURS').length;
      });

      this.systemStats.creneauxDisponibles = creneaux.filter((c: any) => !c.reserve).length;
      this.systemStats.creneauxReserves = creneaux.filter((c: any) => c.reserve).length;

      this.systemStats.revenusMensuel = this.calculateMonthlyRevenue();
    });
  }

  loadRendezVous(): void {
    this.rendezVousService.getRendezVousDuJour().subscribe(rdvs => {
      this.rendezVousDuJour = rdvs.slice(0, 5);
      this.upcomingAppointments = rdvs.slice(0, 3).map((rdv: any) => ({
        heure: this.datePipe.transform(rdv.dateHeure, 'HH:mm'),
        date: this.datePipe.transform(rdv.dateHeure, 'yyyy-MM-dd'),
        patient: rdv.patientNom + ' ' + rdv.patientPrenom,
        medecin: rdv.medecinNom + ' ' + rdv.medecinPrenom,
        type: rdv.typeConsultation || 'Consultation',
        status: rdv.statut
      }));
    });

    this.rendezVousService.getRendezVousUrgents().subscribe(rdvs => {
      this.rendezVousUrgents = rdvs.slice(0, 3);
    });
  }

  loadDemandesValidation(): void {
    this.adminService.listerDemandesValidation().subscribe(demandes => {
      this.demandesValidation = demandes.map(d => ({
        ...d,
        dateCreation: new Date(d['dateInscription'])
      }));
      this.pendingValidations = this.demandesValidation.slice(0, 5);
      this.systemStats.pendingValidations = this.demandesValidation.length;
    });
  }

  loadCreneauxDisponibles(): void {
    // --- CORRECTION: Assurez-vous que l'API reçoit le format attendu, même pour le jour courant ---
    const todayFormatted = this.datePipe.transform(new Date(), 'yyyy-MM-ddTHH:mm:ss')!;
    this.planningService.getCreneauxDisponiblesParDate(todayFormatted).subscribe(creneaux => {
      this.creneauxDisponibles = creneaux.slice(0, 5);
    });
    // --- FIN DE LA CORRECTION ---
  }

  loadChartData(): void {
    // --- CORRECTION: Appliquer le format complet pour la date de début de l'année ---
    const startOfYear = new Date(this.today.getFullYear(), 0, 1);
    startOfYear.setHours(0, 0, 0, 0); // Début de journée pour startOfYear
    this.rendezVousService.getRendezVousBetweenDates(
      this.datePipe.transform(startOfYear, 'yyyy-MM-ddTHH:mm:ss')!,
      this.endOfMonth // endOfMonth est déjà correctement formaté
    ).subscribe(rdvs => {
      this.prepareRendezVousChartData(rdvs);
      this.initRendezVousChart();
    });
    // --- FIN DE LA CORRECTION ---

    this.statistiqueService.getTopMedicamentsPrescrits(5).subscribe(meds => {
      this.chartData.medicamentsPrescrits = meds;
      this.initMedicamentChart();
    });

    this.loadConsultationParSpecialite();
    this.initWeeklyChart();
  }

  prepareRendezVousChartData(rdvs: any[]): void {
    const months = Array(12).fill(0).map((_, i) =>
      this.datePipe.transform(new Date(this.today.getFullYear(), i, 1), 'MMM')!
    );

    const counts = Array(12).fill(0);

    rdvs.forEach(rdv => {
      const month = new Date(rdv.dateHeure).getMonth();
      counts[month]++;
    });

    this.chartData.rendezVousParMois = months.map((month, i) => ({
      month,
      count: counts[i]
    }));
  }

  loadConsultationParSpecialite(): void {
    // --- CORRECTION: Appliquer le format complet pour les dates de consultation par spécialité ---
    const start = new Date(this.today.getFullYear(), this.today.getMonth(), 1);
    start.setHours(0, 0, 0, 0); // Début de journée
    const end = new Date(this.today.getFullYear(), this.today.getMonth() + 1, 0);
    end.setHours(23, 59, 59, 999); // Fin de journée

    this.consultationService.getConsultationsMedecin(
      0,
      this.datePipe.transform(start, 'yyyy-MM-ddTHH:mm:ss')!,
      this.datePipe.transform(end, 'yyyy-MM-ddTHH:mm:ss')!
    ).pipe(
      map(consultations => {
        const rdvObservables = consultations.map(c => this.rendezVousService.getRendezVousById(c.rendezVousId));
        return forkJoin(rdvObservables).pipe(
          map(rdvs => {
            const specialiteMap = new Map<string, number>();
            rdvs.forEach(rdv => {
              const specialite = rdv.serviceNom || 'Autre';
              specialiteMap.set(specialite, (specialiteMap.get(specialite) || 0) + 1);
            });
            return Array.from(specialiteMap.entries()).map(([specialite, count]) => ({
              specialite,
              count
            }));
          })
        );
      })
    ).subscribe(obs => {
      obs.subscribe(data => {
        this.chartData.consultationsParSpecialite = data;
        this.initSpecialiteChart();
      });
    });
    // --- FIN DE LA CORRECTION ---
  }

  initRendezVousChart(): void {
    if (this.rdvChart) this.rdvChart.destroy();

    const ctx = document.getElementById('rdvChart') as HTMLCanvasElement;
    if (ctx) {
      this.rdvChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.chartData.rendezVousParMois.map(d => d.month),
          datasets: [{
            label: 'Rendez-vous par mois',
            data: this.chartData.rendezVousParMois.map(d => d.count),
            borderColor: '#3e95cd',
            backgroundColor: '#7bb6dd',
            fill: true
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: 'Rendez-vous mensuels'
            }
          }
        }
      });
    }
  }

  initMedicamentChart(): void {
    if (this.medicamentChart) this.medicamentChart.destroy();

    const ctx = document.getElementById('medicamentChart') as HTMLCanvasElement;
    if (ctx) {
      this.medicamentChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.chartData.medicamentsPrescrits.map(m => m.nomMedicament),
          datasets: [{
            label: 'Prescriptions',
            data: this.chartData.medicamentsPrescrits.map(m => m.nombrePrescriptions),
            backgroundColor: [
              'rgba(255, 99, 132, 0.7)',
              'rgba(54, 162, 235, 0.7)',
              'rgba(255, 206, 86, 0.7)',
              'rgba(75, 192, 192, 0.7)',
              'rgba(153, 102, 255, 0.7)'
            ]
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: 'Top médicaments prescrits'
            }
          }
        }
      });
    }
  }

  initSpecialiteChart(): void {
    if (this.specialiteChart) this.specialiteChart.destroy();

    const ctx = document.getElementById('specialiteChart') as HTMLCanvasElement;
    if (ctx) {
      this.specialiteChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: this.chartData.consultationsParSpecialite.map(s => s.specialite),
          datasets: [{
            data: this.chartData.consultationsParSpecialite.map(s => s.count),
            backgroundColor: [
              '#3e95cd',
              '#8e5ea2',
              '#3cba9f',
              '#e8c3b9',
              '#c45850'
            ]
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: 'Consultations par spécialité'
            }
          }
        }
      });
    }
  }

  initWeeklyChart(): void {
    if (this.weeklyChart) this.weeklyChart.destroy();

    const ctx = document.getElementById('weeklyChart') as HTMLCanvasElement;
    if (ctx) {
      this.weeklyChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
          datasets: [{
            label: 'Activités hebdomadaires',
            data: [12, 19, 3, 5, 2, 3, 7],
            borderColor: '#ffa726',
            backgroundColor: '#ffb74d',
            fill: true
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: 'Statistiques hebdomadaires'
            }
          }
        }
      });
    }
  }

  calculateMonthlyRevenue(): number {
    const revenuePerConfirmedRdv = 50;
    return this.systemStats.rdvConfirmes * revenuePerConfirmedRdv;
  }

  validerDemande(id: number): void {
    this.adminService.validerDemandeInscription(id, true).subscribe(() => {
      this.demandesValidation = this.demandesValidation.filter(d => d.id !== id);
      this.pendingValidations = this.pendingValidations.filter(d => d.id !== id);
      this.systemStats.pendingValidations = this.demandesValidation.length;
    });
  }

  rejeterDemande(id: number): void {
    this.adminService.validerDemandeInscription(id, false).subscribe(() => {
      this.demandesValidation = this.demandesValidation.filter(d => d.id !== id);
      this.pendingValidations = this.pendingValidations.filter(d => d.id !== id);
      this.systemStats.pendingValidations = this.demandesValidation.length;
    });
  }

  exporterStatistiques(): void {
    this.exportService.exporterPatientsCSV().subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `statistiques-${this.today.toISOString().split('T')[0]}.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  refresh(): void {
    this.loadAllData();
  }

  loadMockData(): void {
    this.recentActivities = [
      { type: 'patient', icon: 'fas fa-user-plus', title: 'Nouveau patient enregistré', description: 'Le patient Jean Dupont a été ajouté.', timestamp: new Date(new Date().setHours(new Date().getHours() - 1)) },
      { type: 'rdv', icon: 'fas fa-calendar-check', title: 'Rendez-vous confirmé', description: 'RDV de Marie Curie avec Dr. Smith.', timestamp: new Date(new Date().setHours(new Date().getHours() - 3)) },
      { type: 'medecin', icon: 'fas fa-user-md', title: 'Mise à jour profil médecin', description: 'Dr. Anna Lee a mis à jour ses disponibilités.', timestamp: new Date(new Date().setDate(new Date().getDate() - 1)) },
      { type: 'system', icon: 'fas fa-cogs', title: 'Sauvegarde automatique', description: 'La sauvegarde quotidienne du système a été effectuée.', timestamp: new Date(new Date().setDate(new Date().getDate() - 2)) },
    ];
  }
}