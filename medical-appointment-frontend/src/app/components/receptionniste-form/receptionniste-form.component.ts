import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { Receptionniste, TypeUtilisateur, UtilisateurDetailDto } from '../../core/models/utilisateur/utilisateur.module';

@Component({
  selector: 'app-receptionniste-form',
  templateUrl: './receptionniste-form.component.html',
  styleUrls: ['./receptionniste-form.component.scss'],
  imports: [ReactiveFormsModule, CommonModule]  
})
export class ReceptionnisteFormComponent implements OnInit {
  receptionnisteForm!: FormGroup;
  receptionnisteId!: number;
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
    this.receptionnisteId = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.loadReceptionniste();
  }

  createForm(): void {
    this.receptionnisteForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      telephone: [''],
      adresse: [''],
      poste: [''],
      plageHoraire: ['']
    });
  }

  loadReceptionniste(): void {
    this.isLoading = true;
    this.adminService.listerUtilisateurs(TypeUtilisateur.RECEPTIONNISTE).subscribe(
      (receptionnistes: UtilisateurDetailDto[]) => {
        const receptionniste = receptionnistes.find(r => r.id === this.receptionnisteId);
        if (receptionniste) {
          this.receptionnisteForm.patchValue(receptionniste);
        }
        this.isLoading = false;
      },
      error => {
        console.error('Erreur lors du chargement du réceptionniste', error);
        this.isLoading = false;
      }
    );
  }

  onSubmit(): void {
    if (this.receptionnisteForm.valid) {
      this.isLoading = true;
      const receptionnisteData: Receptionniste = this.receptionnisteForm.value;
      
      this.adminService.modifierReceptionniste(this.receptionnisteId,receptionnisteData).subscribe(
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
   onCancel(): void {
    // You can add custom logic here, such as resetting the form or navigating away
    this.receptionnisteForm.reset();
  }
}