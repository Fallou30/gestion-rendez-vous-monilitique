import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ModificationAdminDto, TypeUtilisateur, UtilisateurDetailDto } from '../../core/models/utilisateur/utilisateur.module';
import { AdminService } from '../../core/services/admin.service';


@Component({
  selector: 'app-admin-form',
  templateUrl: './admin-form.component.html',
  styleUrls: ['./admin-form.component.scss'],
  imports: [ReactiveFormsModule, CommonModule]  
})
export class AdminFormComponent implements OnInit {
  adminForm!: FormGroup;
  adminId!: number;
  isLoading = false;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.adminId = idParam !== null ? +idParam : 0;
    this.loadAdmin();
  }

  createForm(): void {
    this.adminForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      telephone: [''],
      adresse: ['']
    });
  }

  loadAdmin(): void {
    this.isLoading = true;
    this.adminService.listerUtilisateurs(TypeUtilisateur.ADMIN).subscribe(
      (admins: UtilisateurDetailDto[]) => {
        const admin = admins.find(a => a.id === this.adminId);
        if (admin) {
          this.adminForm.patchValue(admin);
        }
        this.isLoading = false;
      },
      error => {
        console.error('Erreur lors du chargement de l\'administrateur', error);
        this.isLoading = false;
      }
    );
  }

  onSubmit(): void {
    if (this.adminForm.valid) {
      this.isLoading = true;
      const adminData: ModificationAdminDto = this.adminForm.value;
      
      this.adminService.modifierAdministrateur(this.adminId, adminData).subscribe(
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
    this.adminForm.reset();
  }
}