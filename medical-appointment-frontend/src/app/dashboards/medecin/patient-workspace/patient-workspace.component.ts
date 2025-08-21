import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DossierMedicalService } from '../../../core/services/dossier-medical.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { InscriptionService } from '../../../core/services/inscription.service';
import { PatientInfoHeaderComponent } from '../patient-info-header/patient-info-header.component';
import { ConsultationEnCoursComponent } from '../consultation-en-cours/consultation-en-cours.component';
import { DossierMedicalComponent } from '../dossier-medical/dossier-medical.component';
import { PrescriptionComponent } from '../prescription/prescription.component';
import { ExamensComponent } from '../examens/examens.component';
import { ConsultationService } from '../../../core/services/consultation.service';

@Component({
  selector: 'app-patient-workspace',
  templateUrl: './patient-workspace.component.html',
  styleUrls: ['./patient-workspace.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    DossierMedicalComponent,
    ConsultationEnCoursComponent,
    PrescriptionComponent,
    ExamensComponent,
    PatientInfoHeaderComponent
  ]
})
export class PatientWorkspaceComponent implements OnInit {
  // Données du patient
  patient: any = null;
  dossierMedical: any = null;
  rendezVousActuel: any = null;
  
  // États
  isLoading = true;
  error: string | null = null;
  
  // Onglet actif
  activeTab = 'consultation';
  
  // IDs récupérés depuis la route
  patientId!: number;
  rendezVousId!: number;

  // Données partagées entre composants
  consultationData: any = {
    motifConsultation: '',
    symptomes: '',
    examenClinique: '',
    diagnostic: '',
    observations: '',
    planTraitement: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: InscriptionService,
    private dossierMedicalService: DossierMedicalService,
    private rendezVousService: RendezVousService,
    private consultationService: ConsultationService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.patientId = +params['id'];
      this.rendezVousId = this.route.snapshot.queryParams['rendezVousId'];
      this.loadPatientData();
    });
  }

  loadPatientData() {
    this.isLoading = true;
    
    // Charger les données du patient
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.loadDossierMedical();
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des données patient';
        this.isLoading = false;
        console.error('Erreur patient:', error);
      }
    });

    // Charger le rendez-vous actuel si spécifié
    if (this.rendezVousId) {
      this.rendezVousService.getRendezVousById(this.rendezVousId).subscribe({
        next: (rdv) => {
          this.rendezVousActuel = rdv;
        },
        error: (error) => {
          console.error('Erreur rendez-vous:', error);
        }
      });
    }
  }

  loadDossierMedical() {
    this.dossierMedicalService.getDossierPatient(this.patientId).subscribe({
      next: (dossier) => {
        this.dossierMedical = dossier;
        this.isLoading = false;
      },
      error: (error) => {
        if (error.status === 404) {
          // Créer un nouveau dossier médical
          this.creerNouveauDossier();
        } else {
          this.error = 'Erreur lors du chargement du dossier médical';
          this.isLoading = false;
          console.error('Erreur dossier médical:', error);
        }
      }
    });
  }

  creerNouveauDossier() {
  const nouveauDossier = {
    patientId: this.patientId,
    antecedentsMedicaux: '',
    antecedentsFamiliaux: '',
    vaccinations: ''
  };

    this.dossierMedicalService.creerDossier(nouveauDossier).subscribe({
      next: (dossier) => {
        this.dossierMedical = dossier;
        this.isLoading = false;
        this.showNotification('Nouveau dossier médical créé', 'success');
      },
      error: (error) => {
        this.error = 'Erreur lors de la création du dossier médical';
        this.isLoading = false;
        console.error('Erreur création dossier:', error);
      }
    });
  }

  // Navigation entre les onglets
  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  // Gestion des données de consultation
  onConsultationDataChange(data: any) {
    this.consultationData = { ...this.consultationData, ...data };
  }

  // Sauvegarder la consultation
  sauvegarderConsultation() {
    const consultationComplete = {
      ...this.consultationData,
      patientId: this.patientId,
      rendezVousId: this.rendezVousId,
      dateConsultation: new Date().toISOString()
    };

    this.consultationService.creerConsultation(consultationComplete).subscribe({
  next: () => {
    this.showNotification('Consultation sauvegardée avec succès', 'success');
  },
  error: (error: any) => {
    this.showNotification('Erreur lors de la sauvegarde', 'error');
    console.error('Erreur sauvegarde:', error);
  }
  });
  }

  // Terminer la consultation
  terminerConsultation() {
    if (this.rendezVousId) {
      this.rendezVousService.terminerConsultation(this.rendezVousId).subscribe({
        next: () => {
          this.sauvegarderConsultation();
          this.showNotification('Consultation terminée', 'success');
          this.router.navigate(['/medecin/dashboard']);
        },
        error: (error) => {
          this.showNotification('Erreur lors de la finalisation', 'error');
          console.error('Erreur fin consultation:', error);
        }
      });
    }
  }

  // Retour au dashboard
  retourDashboard() {
    this.router.navigate(['/medecin/dashboard']);
  }

  // Imprimer le dossier
  imprimerDossier() {
    window.print();
  }

  // Notifications
  private showNotification(message: string, type: 'success' | 'error' | 'info') {
    console.log(`${type.toUpperCase()}: ${message}`);
  }

  // Utilitaires
  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR');
  }

  calculateAge(birthDate: string): number {
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  }
}