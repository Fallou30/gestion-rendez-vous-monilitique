// src/app/profile/profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DashboardNavComponent } from '../../core/components/dashboard-nav/dashboard-nav.component';
import { AuthService } from '../../core/services/auth.service';
import { ProfilService, ProfilUtilisateur, ChangementMotDePasse } from '../../core/services/profil.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DashboardNavComponent,
    // Tous les modules PrimeNG ont été supprimés ici
  ]
})
export class ProfileComponent implements OnInit {
  activeTab: 'profile' | 'password' = 'profile';
  profil: ProfilUtilisateur | null = null;
  profileForm: FormGroup;
  passwordForm: FormGroup;
  loading = false;
  sexeOptions = ['Masculin', 'Féminin', 'Autre'];
  groupeSanguinOptions = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
  specialiteOptions = ['Cardiologie', 'Dermatologie', 'Neurologie', 'Pédiatrie', 'Radiologie', 'Chirurgie'];

  // Nouvelles propriétés pour la visibilité des mots de passe
  showOldPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  constructor(
    private authService: AuthService,
    private profilService: ProfilService,
    private notificationService: NotificationService,
    private fb: FormBuilder
  ) {
    this.profileForm = this.fb.group({
      // Informations de base
      nom: ['', [Validators.required]],
      prenom: ['', [Validators.required]],
      email: [{ value: '', disabled: true }],
      dateNaissance: [''],
      lieuNaissance: [''],
      sexe: [''],
      // Contact
      adresse: [''],
      telephone: ['', [Validators.pattern(/^[0-9]+$/)]],
      // Médecin
      specialite: [''],
      matricule: [''],
      biographie: [''],
      numeroOrdre: [''],
      experience: [''],
      idService: [''],
      titre: [''],
      // Patient
      profession: [''],
      groupeSanguin: [''],
      allergies: [''],
      contactUrgenceNom: [''],
      contactUrgenceTelephone: [''],
      preferencesNotification: [''],
      // Réceptionniste
      poste: [''],
      idHopital: [''],
      // Admin
      role: [''],
      permissions: ['']
    });

    this.passwordForm = this.fb.group({
      ancienMotDePasse: ['', [Validators.required]],
      nouveauMotDePasse: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/
      )]],
      confirmationMotDePasse: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit() {
    this.chargerProfil();
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('nouveauMotDePasse');
    const confirmPassword = form.get('confirmationMotDePasse');

    if (password?.value !== confirmPassword?.value) {
      confirmPassword?.setErrors({ passwordMismatch: true });
    } else {
      confirmPassword?.setErrors(null);
    }
  }

  // Nouvelles méthodes pour la visibilité des mots de passe
  toggleOldPasswordVisibility(): void {
    this.showOldPassword = !this.showOldPassword;
  }

  toggleNewPasswordVisibility(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  chargerProfil() {
    this.loading = true;
    const userId = this.authService.getCurrentUserId();

    this.profilService.getProfil(userId).subscribe({
      next: (profil) => {
        this.profil = profil;
        // PrimeNG Calendar et Dropdown renvoient des objets Date ou des chaînes spécifiques.
        // Assurez-vous que profil.dateNaissance est un objet Date ou une chaîne 'YYYY-MM-DD'
        // pour que l'input type="date" fonctionne correctement.
        // Si votre backend renvoie une chaîne ISO, c'est généralement bon.
        // Si c'est un objet Date, patchValue le gérera.
        this.profileForm.patchValue({
          nom: profil.nom,
          prenom: profil.prenom,
          email: profil.email,
          dateNaissance: profil.dateNaissance, // Assurez-vous que c'est compatible avec input type="date" (YYYY-MM-DD)
          lieuNaissance: profil.lieuNaissance,
          sexe: profil.sexe,
          adresse: profil.adresse,
          telephone: profil.telephone
        });

        if (profil.medecin) {
          this.profileForm.patchValue({
            specialite: profil.medecin.specialite,
            matricule: profil.medecin.matricule,
            biographie: profil.medecin.biographie,
            numeroOrdre: profil.medecin.numeroOrdre,
            experience: profil.medecin.experience,
            idService: profil.medecin.idService,
            titre: profil.medecin.titre
          });
        }

        if (profil.patient) {
          this.profileForm.patchValue({
            profession: profil.patient.profession,
            groupeSanguin: profil.patient.groupeSanguin,
            allergies: profil.patient.allergies,
            contactUrgenceNom: profil.patient.contactUrgenceNom,
            contactUrgenceTelephone: profil.patient.contactUrgenceTelephone,
            preferencesNotification: profil.patient.preferencesNotification
          });
        }

        if (profil.receptionniste) {
          this.profileForm.patchValue({
            poste: profil.receptionniste.poste,
            idHopital: profil.receptionniste.idHopital
          });
        }

        if (profil.admin) {
          this.profileForm.patchValue({
            role: profil.admin.role,
            permissions: profil.admin.permissions
          });
        }
        this.loading = false;
      },
      error: () => {
        this.notificationService.showError('Impossible de charger le profil');
        this.loading = false;
      }
    });
  }

  modifierProfil() {
    if (this.profileForm.invalid) {
      this.markFormGroupTouched(this.profileForm);
      return;
    }

    this.loading = true;
    const userId = this.authService.getCurrentUserId();
    const formData = this.profileForm.getRawValue();

    this.profilService.updateProfil(userId, formData).subscribe({
      next: (profil) => {
        this.profil = profil;
        this.notificationService.showSuccess('Profil mis à jour avec succès');
        this.loading = false;
      },
      error: () => {
        this.notificationService.showError('Échec de la mise à jour du profil');
        this.loading = false;
      }
    });
  }

  changerMotDePasse() {
    if (this.passwordForm.invalid) {
      this.markFormGroupTouched(this.passwordForm);
      return;
    }

    this.loading = true;
    const userId = this.authService.getCurrentUserId();
    const dto: ChangementMotDePasse = this.passwordForm.value;

    this.profilService.changerMotDePasse(userId, dto).subscribe({
      next: () => {
        this.notificationService.showSuccess('Mot de passe changé avec succès');
        this.passwordForm.reset();
        this.loading = false;
      },
      error: (err) => {
        const msg = err.error?.message || 'Échec du changement de mot de passe';
        this.notificationService.showError(msg);
        this.loading = false;
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  getTypeUtilisateurLabel(): string {
    if (!this.profil) return '';

    switch (this.profil.type) {
      case 'MEDECIN': return 'Médecin';
      case 'PATIENT': return 'Patient';
      case 'RECEPTIONNISTE': return 'Réceptionniste';
      case 'ADMIN': return 'Administrateur';
      case 'SUPER_ADMIN': return 'Super Administrateur';
      default: return this.profil.type;
    }
  }
}