// prise-rendez-vous.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Hopital, Service } from '../../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { Medecin } from '../../../core/models/utilisateur/utilisateur.module';
import { PlanningService } from '../../../core/services/planning.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { CommonModule } from '@angular/common';
import { ServiceService } from '../../../core/services/service.service';
import { MedecinService } from '../../../core/services/medecin.service';
import { HopitalService } from '../../../core/services/hopital.service';

interface PlanningDto {
  idPlanning: number;
  idMedecin: number;
  nomMedecin: string;
  specialiteMedecin: string;
  idService: number;
  nomService: string;
  idHopital: number;
  nomHopital: string;
  adresseHopital: string;
  date: string;
  heureDebut: string;
  heureFin: string;
  reserve: boolean;
}





@Component({
  selector: 'app-prise-rendez-vous',
  templateUrl: './prise-rendez-vous.component.html',
  styleUrls: ['./prise-rendez-vous.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
    // Importer les composants Angular Material ou Bootstrap si nécessaire
})
export class PriseRendezVousComponent implements OnInit {
  rdvForm: FormGroup;
  currentStep = 1;
  totalSteps = 4;
  
  // Données des listes déroulantes
  hopitaux: Hopital[] = [];
  services: Service[] = [];
  medecins: Medecin[] = [];
  creneauxDisponibles: PlanningDto[] = [];
  
  // Filtres et sélections
  selectedHopital?: Hopital;
  selectedService?: Service;
  selectedMedecin?: Medecin;
  selectedCreneau?: PlanningDto;
  selectedDate?: Date;
  
  // États de chargement
  loadingHopitaux = false;
  loadingServices = false;
  loadingMedecins = false;
  loadingCreneaux = false;
  submitting = false;
  
  // Types de consultation
  typesConsultation = [
    { value: 'CONSULTATION_GENERALE', label: 'Consultation Générale', duree: 30, icon: 'medical-bag' },
    { value: 'CONSULTATION_SPECIALISTE', label: 'Consultation Spécialiste', duree: 45, icon: 'user-doctor' },
    { value: 'CONSULTATION_URGENCE', label: 'Consultation d\'Urgence', duree: 20, icon: 'siren' },
    { value: 'CONSULTATION_SUIVI', label: 'Consultation de Suivi', duree: 25, icon: 'clipboard-check' },
    { value: 'CONSULTATION_PREMIERE', label: 'Première Consultation', duree: 30, icon: 'user-plus' }
  ];
  
  // Messages
  successMessage = '';
  errorMessage = '';

 constructor(
    private fb: FormBuilder,
    private planningService: PlanningService,
    private rendezVousService: RendezVousService,
    // Ajoutez les services manquants
    private hopitalService: HopitalService,
    private serviceService: ServiceService,
    private medecinService: MedecinService
  ) {
    this.rdvForm = this.fb.group({
      hopital: ['', Validators.required],
      service: ['', Validators.required],
      medecin: ['', Validators.required],
      date: ['', Validators.required],
      creneau: ['', Validators.required],
      typeConsultation: ['CONSULTATION_GENERALE', Validators.required],
      motif: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
    });
  }

  ngOnInit() {
    this.loadHopitaux();
  }

  // Navigation entre les étapes
  nextStep() {
    if (this.currentStep < this.totalSteps) {
      this.currentStep++;
    }
  }

  prevStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  goToStep(step: number) {
    this.currentStep = step;
  }

  // Vérification de validité des étapes
  isStepValid(step: number): boolean {
  switch (step) {
    case 1:
      return !!this.rdvForm.get('hopital')?.valid && !!this.rdvForm.get('service')?.valid;
    case 2:
      return !!this.rdvForm.get('medecin')?.valid;
    case 3:
      return !!this.rdvForm.get('date')?.valid && !!this.rdvForm.get('creneau')?.valid;
    case 4:
      return !!this.rdvForm.get('typeConsultation')?.valid && !!this.rdvForm.get('motif')?.valid;
    default:
      return false;
  }
}

  // Chargement des données
 // Correction de la méthode loadHopitaux
  loadHopitaux() {
    this.loadingHopitaux = true;
    
    this.hopitalService.getAllHopitaux().subscribe({
      next: (hopitaux) => {
        this.hopitaux = hopitaux;
        this.loadingHopitaux = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des hôpitaux:', error);
        this.errorMessage = 'Impossible de charger les hôpitaux';
        this.loadingHopitaux = false;
      }
    });
  }

  onHopitalChange() {
    const hopitalId = this.rdvForm.get('hopital')?.value;
    this.selectedHopital = this.hopitaux.find(h => h.idHopital == hopitalId);
    
    if (hopitalId) {
      this.loadServices(hopitalId);
      this.resetServiceDependentFields();
    }
  }

  // Correction de la méthode loadServices
  loadServices(hopitalId: number) {
    this.loadingServices = true;
    
    this.serviceService.getServicesByHopital(hopitalId).subscribe({
      next: (services) => {
        this.services = services;
        this.loadingServices = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des services:', error);
        this.errorMessage = 'Impossible de charger les services';
        this.loadingServices = false;
      }
    });
  }

  onServiceChange() {
    const serviceId = this.rdvForm.get('service')?.value;
    this.selectedService = this.services.find(s => s.idService == serviceId);
    
    if (serviceId && this.selectedHopital) {
      this.loadMedecins(this.selectedHopital.idHopital, serviceId);
      this.resetMedecinDependentFields();
    }
  }

 loadMedecins(hopitalId: number, serviceId: number) {
    this.loadingMedecins = true;
    
    this.medecinService.getMedecinsByServiceAndHopital(serviceId, hopitalId).subscribe({
      next: (medecins) => {
        this.medecins = medecins;
        this.loadingMedecins = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des médecins:', error);
        this.errorMessage = 'Impossible de charger les médecins';
        this.loadingMedecins = false;
      }
    });
  }

  onMedecinChange() {
    const medecinId = this.rdvForm.get('medecin')?.value;
    this.selectedMedecin = this.medecins.find(m => m.id == medecinId);
    this.resetCreneauDependentFields();
  }

  onDateChange() {
    const dateStr = this.rdvForm.get('date')?.value;
    if (dateStr && this.selectedMedecin && this.selectedService && this.selectedHopital) {
      this.selectedDate = new Date(dateStr);
      this.loadCreneauxDisponibles();
    }
  }

  loadCreneauxDisponibles() {
    if (!this.selectedMedecin || !this.selectedService || !this.selectedHopital || !this.selectedDate) {
      return;
    }

    this.loadingCreneaux = true;
    
    const dateStr = this.selectedDate.toISOString().split('T')[0];
    
    this.planningService.getCreneauxDisponiblesParCriteres(
      this.selectedMedecin.id,
      this.selectedService.idService,
      this.selectedHopital.idHopital,
      dateStr
    ).subscribe({
      next: (creneaux) => {
        this.creneauxDisponibles = creneaux;
        this.loadingCreneaux = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des créneaux:', error);
        this.errorMessage = 'Impossible de charger les créneaux disponibles';
        this.loadingCreneaux = false;
      }
    });
  }

  onCreneauChange() {
    const creneauId = this.rdvForm.get('creneau')?.value;
    this.selectedCreneau = this.creneauxDisponibles.find(c => c.idPlanning == creneauId);
  }

  // Réinitialisation des champs dépendants
  resetServiceDependentFields() {
    this.rdvForm.patchValue({
      service: '',
      medecin: '',
      date: '',
      creneau: ''
    });
    this.services = [];
    this.resetMedecinDependentFields();
  }

  resetMedecinDependentFields() {
    this.rdvForm.patchValue({
      medecin: '',
      date: '',
      creneau: ''
    });
    this.medecins = [];
    this.resetCreneauDependentFields();
  }

  resetCreneauDependentFields() {
    this.rdvForm.patchValue({
      date: '',
      creneau: ''
    });
    this.creneauxDisponibles = [];
  }

  // Soumission du formulaire
  onSubmit() {
    if (this.rdvForm.valid && this.selectedCreneau) {
      this.submitting = true;
      
      const reservationData = {
        idPlanning: this.selectedCreneau.idPlanning,
        idPatient: 1, // ID du patient connecté (à récupérer du service d'auth)
        typeConsultation: this.rdvForm.get('typeConsultation')?.value,
        motif: this.rdvForm.get('motif')?.value
      };

      this.planningService.reserverCreneau(reservationData).subscribe({
        next: (result) => {
          this.successMessage = 'Votre rendez-vous a été pris avec succès !';
          this.errorMessage = '';
          this.submitting = false;
          
          // Réinitialiser le formulaire après 3 secondes
          setTimeout(() => {
            this.resetForm();
          }, 3000);
        },
        error: (error) => {
          this.errorMessage = 'Une erreur est survenue lors de la prise de rendez-vous. Veuillez réessayer.';
          this.successMessage = '';
          this.submitting = false;
          console.error('Erreur:', error);
        }
      });
    }
  }

  resetForm() {
    this.rdvForm.reset();
    this.currentStep = 1;
    this.selectedHopital = undefined;
    this.selectedService = undefined;
    this.selectedMedecin = undefined;
    this.selectedCreneau = undefined;
    this.selectedDate = undefined;
    this.services = [];
    this.medecins = [];
    this.creneauxDisponibles = [];
    this.successMessage = '';
    this.errorMessage = '';
  }

  // Utilitaires
  getMinDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  getMaxDate(): string {
    const maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 2);
    return maxDate.toISOString().split('T')[0];
  }

  getTypeConsultationInfo(type: string) {
    return this.typesConsultation.find(t => t.value === type);
  }
}