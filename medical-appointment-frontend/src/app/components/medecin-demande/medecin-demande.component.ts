import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { HopitalService } from '../../core/services/hopital.service';
import { MedecinService } from '../../core/services/medecin.service';
import {
  Hopital,
  Service,
} from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { DemandeMedecin } from '../../core/models/utilisateur/utilisateur.module';
import { ServiceService } from '../../core/services/service.service';

@Component({
  selector: 'app-medecin-demande',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './medecin-demande.component.html',
  styleUrls: ['./medecin-demande.component.scss'],
})
export class MedecinDemandeComponent implements OnInit {
  demandeForm: FormGroup;
  hopitaux: Hopital[] = [];
  services: Service[] = [];

  selectedFiles: {
    diplome?: File;
    carteOrdre?: File;
    cv?: File;
  } = {};
  uploadProgress = {
    diplome: 0,
    carteOrdre: 0,
    cv: 0,
  };
   
  isSubmitting = false;
  message = '';
  messageType: 'success' | 'error' | 'warning' = 'success';

  constructor(
    private medecinService: MedecinService,
    private serviceService: ServiceService,
    private fb: FormBuilder,
    private hopitalService: HopitalService,
    private router: Router
  ) {
    this.demandeForm = this.fb.group({
      nom: ['', [Validators.required, Validators.maxLength(50)]],
      prenom: ['', [Validators.required, Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: [
        '',
        [Validators.required, Validators.pattern(/^[0-9+\-\s]+$/)],
      ],
      dateNaissance: ['', Validators.required],
      adresse: ['', [Validators.required, Validators.maxLength(200)]],
      lieuNaissance: ['', [Validators.required, Validators.maxLength(100)]],
      sexe: ['', Validators.required],
      matricule: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[A-Z0-9]+$/),
          Validators.maxLength(20),
        ],
      ],
      specialite: ['', [Validators.required, Validators.maxLength(100)]],
      titre: [''],
      numeroOrdre: ['', [Validators.required, Validators.maxLength(20)]],
      experience: [0, [Validators.min(0), Validators.max(50)]],
      biographie: [''],
      idHopital: ['', Validators.required],
      idService: ['', Validators.required],
      motifDemande: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.chargerHopitaux();

    // Lorsqu’on change d’hôpital, charger les services associés
    this.demandeForm
      .get('idHopital')
      ?.valueChanges.subscribe((idHopital: number) => {
        if (idHopital) {
          this.chargerServicesParHopital(idHopital);
          this.demandeForm.get('idService')?.reset();
        } else {
          this.services = [];
        }
      });
  }

  chargerHopitaux() {
    this.hopitalService.getAllHopitaux().subscribe({
      next: (hopitaux) => {
        this.hopitaux = hopitaux;
      },
      error: () =>
        this.afficherMessage('Erreur lors du chargement des hôpitaux', 'error'),
    });
  }

  chargerServicesParHopital(idHopital: number) {
    this.serviceService.getServicesByHopital(idHopital).subscribe({
      next: (services) => (this.services = services),
      error: () =>
        this.afficherMessage('Erreur lors du chargement des services', 'error'),
    });
  }

  onFileSelect(event: Event, type: 'diplome' | 'carteOrdre' | 'cv') {
    const target = event.target as HTMLInputElement;
    if (target.files?.length) {
      const file = target.files[0];

      if (file.size > 5 * 1024 * 1024) {
        this.afficherMessage('Le fichier ne peut pas dépasser 5MB', 'error');
        return;
      }

      const allowedTypes = [
        'application/pdf',
        'image/jpeg',
        'image/jpg',
        'image/png',
      ];
      if (!allowedTypes.includes(file.type)) {
        this.afficherMessage(
          'Type de fichier non autorisé. Utilisez PDF, JPG ou PNG',
          'error'
        );
        return;
      }

      this.selectedFiles[type] = file;
    }
  }

  async onSubmit() {
    if (this.demandeForm.invalid) {
      this.afficherMessage('Veuillez remplir tous les champs requis', 'error');
      this.markFormGroupTouched(this.demandeForm);
      return;
    }

    this.isSubmitting = true;

    try {
      // Construire l'objet DemandeMedecin à partir du formulaire
      const formValue = this.demandeForm.value;
      const demande: DemandeMedecin = {
        ...formValue,
        idsHopitaux: [formValue.idHopital], // l'API attend un tableau d'hôpitaux
        cvPath: '',
        diplomePath: '',
        carteOrdrePath: '',
      };

      // Appel service qui gère upload fichiers + création de demande
      await this.medecinService
        .demanderInscriptionMedecin(
          demande,
          this.selectedFiles.cv,
          this.selectedFiles.diplome,
          this.selectedFiles.carteOrdre
        )
        .toPromise();

      this.afficherMessage(
        "Votre demande a été soumise avec succès. Vous recevrez une notification par email une fois qu'elle sera traitée.",
        'success'
      );

      setTimeout(() => {
        this.resetForm();
        this.router.navigate(['/']);
      }, 3000);
    } catch (error) {
      this.afficherMessage(
        'Erreur lors de la soumission de la demande',
        'error'
      );
    } finally {
      this.isSubmitting = false;
    }
  }
  getMessageIcon(): string {
    switch (this.messageType) {
      case 'success':
        return 'ff ff-check-circle';
      case 'error':
        return 'ff ff-times-circle';
      case 'warning':
        return 'ff ff-exclamation-triangle';
      default:
        return 'ff ff-info-circle';
    }
  }

  resetForm() {
    this.demandeForm.reset();
    this.selectedFiles = {};
    this.services = [];
    this.message = '';
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach((key) => {
      formGroup.get(key)?.markAsTouched();
    });
  }

  private afficherMessage(
    message: string,
    type: 'success' | 'error' | 'warning'
  ) {
    this.message = message;
    this.messageType = type;
    setTimeout(() => (this.message = ''), 5000);
  }
}
