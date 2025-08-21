import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { CommonModule } from '@angular/common';
import {
  ModificationMedecinDto,
  TypeUtilisateur,
  UtilisateurDetailDto,
} from '../../core/models/utilisateur/utilisateur.module';
import { AdminService } from '../../core/services/admin.service';
import { FileUploadService } from '../../core/services/file-upload.service';

@Component({
  selector: 'app-medecin-form',
  templateUrl: './medecin-form.component.html',
  styleUrls: ['./medecin-form.component.scss'],
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  standalone: true,
})
export class MedecinFormComponent implements OnInit {
  medecinForm!: FormGroup;
  medecinId!: number;
  isLoading = false;
  cvFile?: File;
  diplomeFile?: File;
  carteOrdreFile?: File;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private adminService: AdminService,
    private fileService: FileUploadService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.medecinId = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.loadMedecin();
  }

  createForm(): void {
    this.medecinForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateNaissance: [''],
      lieuNaissance: [''],
      sexe: [''],
      adresse: [''],
      telephone: [''],
      email: ['', [Validators.required, Validators.email]],
      titre: [''],
      specialite: [''],
      matricule: [''],
      experience: [0],
      biographie: [''],
    });
  }
  onFileSelected(event: Event, type: 'cv' | 'diplome' | 'carteOrdre'): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (type === 'cv') this.cvFile = file;
      if (type === 'diplome') this.diplomeFile = file;
      if (type === 'carteOrdre') this.carteOrdreFile = file;
    }
  }

  loadMedecin(): void {
    this.isLoading = true;
    this.adminService.listerUtilisateurs(TypeUtilisateur.MEDECIN).subscribe(
      (medecins: UtilisateurDetailDto[]) => {
        const medecin = medecins.find((m) => m.id === this.medecinId);
        if (medecin) {
          this.medecinForm.patchValue(medecin);
        }
        this.isLoading = false;
      },
      (error) => {
        console.error('Erreur lors du chargement du médecin', error);
        this.isLoading = false;
      }
    );
  }

  onSubmit(): void {
    if (this.medecinForm.valid) {
      this.isLoading = true;
      const medecinData: ModificationMedecinDto = this.medecinForm.value;
      // Upload les documents un par un si fournis
      const uploads: Promise<void>[] = [];

      const uploadAndAssign = (
        file: File,
        type: 'cv' | 'diplome' | 'carteOrdre'
      ) => {
        return this.fileService
          .uploadMedecinDocument(this.medecinId, file, type)
          .toPromise()
          .then((res) => {
            if (!res || !res.filePath) return;

            if (type === 'cv') medecinData.cvPath = res.filePath;
            if (type === 'diplome') medecinData.diplomePath = res.filePath;
            if (type === 'carteOrdre')
              medecinData.carteOrdrePath = res.filePath;
          });
      };
      if (this.cvFile) uploads.push(uploadAndAssign(this.cvFile, 'cv'));
      if (this.diplomeFile)
        uploads.push(uploadAndAssign(this.diplomeFile, 'diplome'));
      if (this.carteOrdreFile)
        uploads.push(uploadAndAssign(this.carteOrdreFile, 'carteOrdre'));

      Promise.all(uploads)
        .then(() => {
          this.adminService
            .modifierMedecin(this.medecinId, medecinData)
            .subscribe(
              () => this.router.navigate(['/admin/users']),
              (error) => {
                console.error('Erreur mise à jour médecin', error);
                this.isLoading = false;
              }
            );
        })
        .catch((err) => {
          console.error('Erreur upload fichier(s)', err);
          this.isLoading = false;
        });
    }
  }
  goToMedecinsList() {
    this.router.navigate(['/admin/users']);
  }
  onCancel(): void {
    // You can add custom logic here, such as resetting the form or navigating away
    this.medecinForm.reset();
  }
}
