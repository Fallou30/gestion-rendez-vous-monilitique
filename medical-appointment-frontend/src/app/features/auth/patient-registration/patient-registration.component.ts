import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PatientService } from '../../../core/services/patient.service';
import { InscriptionPatient } from '../../../core/models/utilisateur/utilisateur.module';

@Component({
  selector: 'app-patient-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './patient-registration.component.html',
  styleUrls: ['./patient-registration.component.scss']
})
export class PatientRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  emailAvailable = true;
  currentYear = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private router: Router
  ) {
    this.registrationForm = this.fb.group({
      nom: ['', [Validators.required]],
      prenom: ['', [Validators.required]],
      dateNaissance: ['', [Validators.required]],
      lieuNaissance: [''],
      sexe: [''],
      telephone: ['', [Validators.required, Validators.pattern(/^(\+221)?[0-9]{9}$/)]],
      adresse: [''],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required, Validators.minLength(8)]],
      confirmationMotDePasse: ['', [Validators.required]],
      numAssurance: [''],
      groupeSanguin: [''],
      profession: ['', [Validators.required]],
      allergies: [''],
      contactUrgenceNom: ['', [Validators.required]],
      contactUrgenceTelephone: ['', [Validators.required, Validators.pattern(/^(\+221)?[0-9]{9}$/)]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {}

  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('motDePasse');
    const confirmPassword = control.get('confirmationMotDePasse');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  checkEmailAvailability(): void {
    const email = this.registrationForm.get('email')?.value;
    
    if (email && this.registrationForm.get('email')?.valid) {
      this.patientService.emailExiste(email).subscribe({
        next: (response) => {
          this.emailAvailable = response;
        },
        error: (error) => {
          console.error('Erreur vérification email:', error);
          this.emailAvailable = false;
        }
      });
    }
  }

  onSubmit(): void {
    if (this.registrationForm.valid && this.emailAvailable) {
      this.isLoading = true;
      this.errorMessage = '';

      const formData = { ...this.registrationForm.value };
      delete formData.confirmationMotDePasse;

      const inscriptionData: InscriptionPatient = {
        ...formData,
        telephone: this.patientService.formaterTelephone(formData.telephone),
        contactUrgenceTelephone: this.patientService.formaterTelephone(formData.contactUrgenceTelephone)
      };

      this.patientService.inscrirePatient(inscriptionData).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/auth/login'], { 
            state: { 
              registrationSuccess: true,
              email: this.registrationForm.value.email
            }
          });
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Erreur lors de l\'inscription. Veuillez réessayer.';
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/auth/login']);
  }

  getMaxBirthDate(): string {
    const today = new Date();
    return new Date(today.getFullYear() - 18, today.getMonth(), today.getDate()).toISOString().split('T')[0];
  }
}