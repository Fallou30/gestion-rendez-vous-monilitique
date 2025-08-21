import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DashboardNavComponent } from '../../../core/components/dashboard-nav/dashboard-nav.component';
import { RendezVous, RendezVousRequest, StatutRendezVous } from '../../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { TypeUtilisateur } from '../../../core/models/utilisateur/utilisateur.module';
import { AdminService } from '../../../core/services/admin.service';
import { HopitalService } from '../../../core/services/hopital.service';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import { ServiceService } from '../../../core/services/service.service';
import { MatButton } from '@angular/material/button';

interface RendezVousFilter {
  statut: string;
  hopital: number | null;
  service: number | null;
  medecin: number | null;
  dateDebut: string;
  dateFin: string;
  urgence: string;
}

@Component({
  selector: 'app-admin-rendez-vous',
  templateUrl: './admin-rendez-vous.component.html',
  styleUrls: ['./admin-rendez-vous.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    DashboardNavComponent,
  ],
  providers: [DatePipe]
})
export class AdminRendezVousComponent implements OnInit {
  userType = TypeUtilisateur.ADMIN;
  Math = Math;
  // Données
  rendezVousList: RendezVous[] = [];
  filteredRendezVous: RendezVous[] = [];
  hopitaux: any[] = [];
  services: any[] = [];
  medecins: any[] = [];
  patients: any[] = [];
  
  // États
  loading = false;
  showCreateModal = false;
  showEditModal = false;
  showDetailsModal = false;
  selectedRendezVous: RendezVous | null = null;
  
  // Formulaires
  createForm: FormGroup;
  editForm: FormGroup;
  
  // Filtres
  filters: RendezVousFilter = {
    statut: '',
    hopital: null,
    service: null,
    medecin: null,
    dateDebut: '',
    dateFin: '',
    urgence: ''
  };
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalItems = 0;
  
  // Options
  statutOptions = [
    { value: 'EN_ATTENTE', label: 'En attente' },
    { value: 'CONFIRME', label: 'Confirmé' },
    { value: 'EN_COURS', label: 'En cours' },
    { value: 'TERMINE', label: 'Terminé' },
    { value: 'ANNULE', label: 'Annulé' },
    { value: 'REPORTE', label: 'Reporté' }
  ];
  
  urgenceOptions = [
    { value: 'BASSE', label: 'Basse' },
    { value: 'NORMALE', label: 'Normale' },
    { value: 'HAUTE', label: 'Haute' },
    { value: 'URGENCE', label: 'Urgence' }
  ];
  
  // Statistiques
  stats = {
    total: 0,
    enAttente: 0,
    confirmes: 0,
    enCours: 0,
    termines: 0,
    annules: 0,
    enRetard: 0
  };

  constructor(
    private rendezVousService: RendezVousService,
    private adminService: AdminService,
    private serviceService: ServiceService,
    private hopitalService: HopitalService,
    private fb: FormBuilder,
    private datePipe: DatePipe
  ) {
    this.createForm = this.initCreateForm();
    this.editForm = this.initEditForm();
  }

  ngOnInit(): void {
    this.loadInitialData();
  }

  initCreateForm(): FormGroup {
    return this.fb.group({
      patientId: ['', Validators.required],
      medecinId: ['', Validators.required],
      serviceId: ['', Validators.required],
      hopitalId: ['', Validators.required],
      dateHeure: ['', Validators.required],
      dureePrevue: [30, [Validators.required, Validators.min(15)]],
      typeConsultation: ['CONSULTATION', Validators.required],
      niveauUrgence: ['NORMALE', Validators.required],
      motifConsultation: ['', Validators.required],
      notes: ['']
    });
  }

  initEditForm(): FormGroup {
    return this.fb.group({
      id: [''],
      dateHeure: ['', Validators.required],
      dureePrevue: [30, [Validators.required, Validators.min(15)]],
      typeConsultation: ['', Validators.required],
      niveauUrgence: ['', Validators.required],
      motifConsultation: ['', Validators.required],
      notes: [''],
      statut: ['', Validators.required]
    });
  }

  loadInitialData(): void {
    this.loading = true;
    
    // Charger toutes les données nécessaires
    Promise.all([
      this.loadRendezVous(),
      this.loadHopitaux(),
      this.loadServices(),
      this.loadMedecins(),
      this.loadPatients(),
      this.loadStats()
    ]).finally(() => {
      this.loading = false;
    });
  }

  async loadRendezVous(): Promise<void> {
    try {
      this.rendezVousList = await this.rendezVousService.getAllRendezVous().toPromise() || [];
      this.applyFilters();
    } catch (error) {
      console.error('Erreur lors du chargement des rendez-vous:', error);
    }
  }

  async loadHopitaux(): Promise<void> {
    try {
      this.hopitaux = await this.hopitalService.getAllHopitaux().toPromise() || [];
    } catch (error) {
      console.error('Erreur lors du chargement des hôpitaux:', error);
    }
  }

  async loadServices(): Promise<void> {
    try {
      this.services = await this.serviceService.getAllServices().toPromise() || [];
    } catch (error) {
      console.error('Erreur lors du chargement des services:', error);
    }
  }

  async loadMedecins(): Promise<void> {
    try {
      this.medecins = await this.adminService.listerUtilisateurs(TypeUtilisateur.MEDECIN).toPromise() || [];
    } catch (error) {
      console.error('Erreur lors du chargement des médecins:', error);
    }
  }

  async loadPatients(): Promise<void> {
    try {
      this.patients = await this.adminService.listerUtilisateurs(TypeUtilisateur.PATIENT).toPromise() || [];
    } catch (error) {
      console.error('Erreur lors du chargement des patients:', error);
    }
  }

  async loadStats(): Promise<void> {
    try {
      const rendezVous = this.rendezVousList;
      this.stats = {
        total: rendezVous.length,
        enAttente: rendezVous.filter(rdv => rdv.statut =="PLANIFIE" ).length,
        confirmes: rendezVous.filter(rdv => rdv.statut === 'CONFIRME').length,
        enCours: rendezVous.filter(rdv => rdv.statut === 'EN_COURS').length,
        termines: rendezVous.filter(rdv => rdv.statut === 'TERMINE').length,
        annules: rendezVous.filter(rdv => rdv.statut === 'ANNULE').length,
        enRetard: 0 // À calculer selon la logique métier
      };
      
      // Charger les rendez-vous en retard
      const enRetard = await this.rendezVousService.getRendezVousEnRetard().toPromise() || [];
      this.stats.enRetard = enRetard.length;
    } catch (error) {
      console.error('Erreur lors du calcul des statistiques:', error);
    }
  }

  applyFilters(): void {
    let filtered = [...this.rendezVousList];

    // Filtre par statut
    if (this.filters.statut) {
      filtered = filtered.filter(rdv => rdv.statut === this.filters.statut);
    }

    // Filtre par hôpital
    if (this.filters.hopital) {
      filtered = filtered.filter(rdv => rdv.hopitalId === this.filters.hopital);
    }

    // Filtre par service
    if (this.filters.service) {
      filtered = filtered.filter(rdv => rdv.serviceId === this.filters.service);
    }

    // Filtre par médecin
    if (this.filters.medecin) {
      filtered = filtered.filter(rdv => rdv.medecinId === this.filters.medecin);
    }

    // Filtre par urgence
    if (this.filters.urgence) {
      filtered = filtered.filter(rdv => rdv.niveauUrgence === this.filters.urgence);
    }

    // Filtre par dates
    if (this.filters.dateDebut) {
      filtered = filtered.filter(rdv => 
        new Date(rdv.dateHeure) >= new Date(this.filters.dateDebut)
      );
    }

    if (this.filters.dateFin) {
      filtered = filtered.filter(rdv => 
        new Date(rdv.dateHeure) <= new Date(this.filters.dateFin)
      );
    }

    this.filteredRendezVous = filtered;
    this.totalItems = filtered.length;
  }

  clearFilters(): void {
    this.filters = {
      statut: '',
      hopital: null,
      service: null,
      medecin: null,
      dateDebut: '',
      dateFin: '',
      urgence: ''
    };
    this.applyFilters();
  }

  getPaginatedData(): RendezVous[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredRendezVous.slice(startIndex, endIndex);
  }

  onPageChange(page: number): void {
    this.currentPage = page;
  }

  openCreateModal(): void {
    this.createForm.reset();
    this.createForm.patchValue({
      dureePrevue: 30,
      typeConsultation: 'CONSULTATION',
      niveauUrgence: 'NORMALE'
    });
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createForm.reset();
  }

  async onCreateSubmit(): Promise<void> {
    if (this.createForm.valid) {
      try {
        const formData = this.createForm.value;
        const rdvRequest: RendezVousRequest = {
          idPatient: parseInt(formData.patientId),
          idMedecin: parseInt(formData.medecinId),
          idService: parseInt(formData.serviceId),
          idHopital: parseInt(formData.hopitalId),
          dateHeure: formData.dateHeure,
          dureePrevue: formData.dureePrevue,
          typeConsultation: formData.typeConsultation,
          niveauUrgence: formData.niveauUrgence,
          motif: formData.motifConsultation,
        };

        await this.rendezVousService.createRendezVous(rdvRequest).toPromise();
        this.closeCreateModal();
        this.loadRendezVous();
        this.loadStats();
      } catch (error) {
        console.error('Erreur lors de la création du rendez-vous:', error);
      }
    }
  }

  openEditModal(rendezVous: RendezVous): void {
    this.selectedRendezVous = rendezVous;
    this.editForm.patchValue({
      id: rendezVous.idRdv,
      dateHeure: this.datePipe.transform(rendezVous.dateHeure, 'yyyy-MM-ddTHH:mm'),
      dureePrevue: rendezVous.dureePrevue,
      typeConsultation: rendezVous.typeConsultation,
      niveauUrgence: rendezVous.niveauUrgence,
      motifConsultation: rendezVous.motif,
      statut: rendezVous.statut
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedRendezVous = null;
    this.editForm.reset();
  }

  async onEditSubmit(): Promise<void> {
    if (this.editForm.valid && this.selectedRendezVous) {
      try {
        const formData = this.editForm.value;
        const updatedRdv: RendezVous = {
          ...this.selectedRendezVous,
          dateHeure: formData.dateHeure,
          dureePrevue: formData.dureePrevue,
          typeConsultation: formData.typeConsultation,
          niveauUrgence: formData.niveauUrgence,
          motif: formData.motifConsultation,
          statut: formData.statut
        };

        await this.rendezVousService.updateRendezVousParRv(this.selectedRendezVous.idRdv!, updatedRdv).toPromise();
        this.closeEditModal();
        this.loadRendezVous();
        this.loadStats();
      } catch (error) {
        console.error('Erreur lors de la modification du rendez-vous:', error);
      }
    }
  }

  openDetailsModal(rendezVous: RendezVous): void {
    this.selectedRendezVous = rendezVous;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedRendezVous = null;
  }

  async changeStatut(rendezVous: RendezVous, newStatut: string): Promise<void> {
    try {
      await this.rendezVousService.changeStatutRendezVous(rendezVous.idRdv!, newStatut).toPromise();
      this.loadRendezVous();
      this.loadStats();
    } catch (error) {
      console.error('Erreur lors du changement de statut:', error);
    }
  }

  async confirmerRendezVous(rendezVous: RendezVous): Promise<void> {
    try {
      await this.rendezVousService.confirmerRendezVous(rendezVous.idRdv!).toPromise();
      this.loadRendezVous();
      this.loadStats();
    } catch (error) {
      console.error('Erreur lors de la confirmation:', error);
    }
  }

  async annulerRendezVous(rendezVous: RendezVous): Promise<void> {
    if (confirm('Êtes-vous sûr de vouloir annuler ce rendez-vous ?')) {
      try {
        await this.rendezVousService.annulerRendezVous(rendezVous.idRdv!).toPromise();
        this.loadRendezVous();
        this.loadStats();
      } catch (error) {
        console.error('Erreur lors de l\'annulation:', error);
      }
    }
  }

  async deleteRendezVous(rendezVous: RendezVous): Promise<void> {
    if (confirm('Êtes-vous sûr de vouloir supprimer définitivement ce rendez-vous ?')) {
      try {
        await this.rendezVousService.deleteRendezVous(rendezVous.idRdv!).toPromise();
        this.loadRendezVous();
        this.loadStats();
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
      }
    }
  }

  getStatutClass(statut: string): string {
    const classes: { [key: string]: string } = {
      'EN_ATTENTE': 'badge-warning',
      'CONFIRME': 'badge-info',
      'EN_COURS': 'badge-primary',
      'TERMINE': 'badge-success',
      'ANNULE': 'badge-danger',
      'REPORTE': 'badge-secondary'
    };
    return classes[statut] || 'badge-secondary';
  }

  getUrgenceClass(urgence: string): string {
    const classes: { [key: string]: string } = {
      'BASSE': 'badge-success',
      'NORMALE': 'badge-info',
      'HAUTE': 'badge-warning',
      'URGENCE': 'badge-danger'
    };
    return classes[urgence] || 'badge-secondary';
  }

  exportRendezVous(): void {
    // Logique d'export (CSV, Excel, etc.)
    console.log('Export des rendez-vous...');
  }

  refresh(): void {
    this.loadInitialData();
  }
}