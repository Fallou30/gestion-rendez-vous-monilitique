import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ServiceService } from '../../core/services/service.service';
import { HopitalService } from '../../core/services/hopital.service';
import { Service, StatutService, Hopital } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { StatutUtilisateur, TypeUtilisateur } from '../../core/models/utilisateur/utilisateur.module';

@Component({
  selector: 'app-service-form',
  templateUrl: './service-form.component.html',
  styleUrls: ['./service-form.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class ServiceFormComponent implements OnInit {
  @Input() service: Service | null = null;
  serviceForm!: FormGroup;
  statutOptions = Object.values(StatutService);
  hopitaux: Hopital[] = [];
  medecins: any[] = [];
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private serviceService: ServiceService,
    private hopitalService: HopitalService,
    private adminService: AdminService

  ) {}

  ngOnInit(): void {
    this.isEdit = !!this.service?.idService;

    this.serviceForm = this.fb.group({
      nom: [this.service?.nom || '', Validators.required],
      description: [this.service?.description || ''],
      emplacement: [this.service?.emplacement || ''],
      telephone: [this.service?.telephone || '', [Validators.pattern(/^[0-9]{9}$/)]],
      email: [this.service?.email || '', [Validators.email]],
      capacitePatientsJour: [this.service?.capacitePatientsJour || 0],
      statut: [this.service?.statut || StatutService.ACTIF, Validators.required],
      hopital: [this.service?.idHopital || '', Validators.required],
      chefService: [this.service?.idChefService || '', Validators.required]
    });

    this.loadHopitaux();
    this.loadMedecins
    
   
  }

  loadHopitaux(): void {
  this.hopitalService.getAllHopitaux().subscribe({
    next: (data) => this.hopitaux = data,
    error: (err) => console.error('Erreur chargement hôpitaux', err)
  });
  }
  loadMedecins(): void {
    this.adminService.listerUtilisateurs(TypeUtilisateur.MEDECIN,StatutUtilisateur.ACTIF).subscribe({
      next: (data) => this.medecins = data,
      error: (err) => console.error('Erreur chargement médecins', err)
    });
  }

  onSubmit(): void {
    if (this.serviceForm.invalid) return;

    const formValue = this.serviceForm.value;

    // Préparation de l'objet Service avec objets complets si nécessaire
    const preparedService: Service = {
      ...formValue,
      hopital: this.hopitaux.find(h => h.idHopital === formValue.hopital),
      chefService: this.medecins.find(m => m.id === formValue.chefService)
    };

    if (this.isEdit && this.service?.idService) {
      this.serviceService.updateService(this.service.idService, preparedService).subscribe({
        next: () => alert('Service modifié avec succès !'),
        error: () => alert('Erreur lors de la modification.')
      });
    } else {
      this.serviceService.createService(preparedService).subscribe({
        next: () => {
          alert('Service ajouté avec succès !');
          this.serviceForm.reset();
        },
        error: () => alert('Erreur lors de l’ajout.')
      });
    }
  }
}
