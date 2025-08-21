import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PatientService } from '../../core/services/patient.service';
import { TypeUtilisateur, UtilisateurDetailDto, ModificationPatient } from '../../core/models/utilisateur/utilisateur.module';
import { AdminService } from '../../core/services/admin.service';

@Component({
  selector: 'app-patient-form',
  templateUrl: './patient-form.component.html',
  styleUrls: ['./patient-form.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class PatientFormComponent implements OnInit {
  patientForm!: FormGroup;
  patientId!: number;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private adminService: AdminService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.patientId = idParam !== null ? +idParam : 0;
    this.loadPatient();
  }

  createForm(): void {
    this.patientForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateNaissance: [''],
      lieuNaissance: [''],
      sexe: [''],
      adresse: [''],
      telephone: [''],
      numAssurance: [''],
      groupeSanguin: [''],
      allergies: [''],
      contactUrgenceNom: [''],
      contactUrgenceTelephone: [''],
      profession: [''],
      preferenceNotification:['']
    });
  }

  loadPatient(): void {
    this.isLoading = true;
    this.adminService.listerUtilisateurs(TypeUtilisateur.PATIENT).subscribe(
      (patients: UtilisateurDetailDto[]) => {
        const patient = patients.find(p => p.id === this.patientId);
        if (patient) {
          this.patientForm.patchValue(patient);
        }
        this.isLoading = false;
      },
      error => {
        console.error('Erreur lors du chargement du patient', error);
        this.isLoading = false;
      }
    );
  }

  onSubmit(): void {
    if (this.patientForm.valid) {
      this.isLoading = true;
      const patientData: ModificationPatient = this.patientForm.value;
      
      this.adminService.modifierPatient(this.patientId, patientData).subscribe(
        () => {
          this.router.navigate(['/admin/users']);
        },
        error => {
          console.error('Erreur lors de la mise à jour', error);
          this.isLoading = false;
        }
      );
    }
  }
  goToUsers(): void {
   this.router.navigate(['/admin/users']);
  }
  onCancel(): void {
    // You can add custom logic here, such as resetting the form or navigating away
    this.patientForm.reset();
  }
}