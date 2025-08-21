// src/app/components/user-management/user-management.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Utilisateur, TypeUtilisateur, StatutUtilisateur, UtilisateurDetailDto } from '../../core/models/utilisateur/utilisateur.module';
import { AdminService } from '../../core/services/admin.service';
import { ModalComponent } from '../modal/modal.component';


@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ModalComponent],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
})
export class UserManagementComponent implements OnInit {
 utilisateurs: UtilisateurDetailDto[] = [];
  filteredUsers: UtilisateurDetailDto[] = [];
  filtreType: TypeUtilisateur | undefined = undefined;
  filtreStatut: StatutUtilisateur | undefined = undefined;
  searchTerm = '';
  loading = false;
  message = '';
  messageType: 'success' | 'error' | 'warning' = 'success';
  currentPage = 1;
  itemsPerPage = 10;
  showDeleteModal = false;
  userToDelete: UtilisateurDetailDto | null = null;
  StatutUtilisateur = StatutUtilisateur;
  TypeUtilisateur = TypeUtilisateur;


  constructor(
    private adminService: AdminService,
    private router: Router
  ) {}
  ngOnInit() {
    this.chargerUtilisateurs();
  }

  chargerUtilisateurs() {
    this.loading = true;
    this.adminService.listerUtilisateurs(this.filtreType, this.filtreStatut)
      .subscribe({
        next: (utilisateurs) => {
          this.utilisateurs = utilisateurs;
          this.filteredUsers = [...utilisateurs];
          this.loading = false;
        },
        error: (error) => {
          this.afficherMessage('Erreur lors du chargement des utilisateurs', 'error');
          this.loading = false;
        }
      });
  }

  applyFilters() {
    this.filteredUsers = this.utilisateurs.filter(user => {
      const matchesType = !this.filtreType || user.type === this.filtreType;
      const matchesStatut = !this.filtreStatut || user.statut === this.filtreStatut;
      const matchesSearch = !this.searchTerm || 
        user.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
        user.prenom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());

      return matchesType && matchesStatut && matchesSearch;
    });
    this.currentPage = 1;
  }

  modifierUtilisateur(utilisateur: UtilisateurDetailDto) {
    switch(utilisateur.type) {
      case TypeUtilisateur.MEDECIN:
        this.router.navigate(['/admin/medecins/modifier', utilisateur.id]);
        break;
      case TypeUtilisateur.RECEPTIONNISTE:
        this.router.navigate(['/admin/receptionnistes/modifier', utilisateur.id]);
        break;
      case TypeUtilisateur.ADMIN:
      case TypeUtilisateur.SUPER_ADMIN:
        this.router.navigate(['/admin/administrateurs/modifier', utilisateur.id]);
        break;
      case TypeUtilisateur.PATIENT:
        this.router.navigate(['/admin/patients/modifier', utilisateur.id]);
        break;
      default:
        console.error('Type d\'utilisateur non reconnu:', utilisateur.type);
        this.afficherMessage('Type d\'utilisateur non reconnu', 'error');
        break;
    }
  }

  changerStatutUtilisateur(utilisateur: UtilisateurDetailDto, nouveauStatut: StatutUtilisateur) {
    if (!confirm(`Êtes-vous sûr de vouloir changer le statut de ${utilisateur.prenom} ${utilisateur.nom} en ${this.getStatutLabel(nouveauStatut)}?`)) {
      return;
    }

    this.loading = true;
    this.adminService.changerStatutUtilisateur(utilisateur.id!, nouveauStatut)
      .subscribe({
        next: () => {
          this.afficherMessage('Statut utilisateur modifié avec succès', 'success');
          this.chargerUtilisateurs();
        },
        error: (error) => {
          this.afficherMessage('Erreur lors de la modification du statut', 'error');
          this.loading = false;
        }
      });
  }


  get paginatedUsers(): UtilisateurDetailDto[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  getTypeLabel(type: TypeUtilisateur): string {
    const labels = {
      [TypeUtilisateur.PATIENT]: 'Patient',
      [TypeUtilisateur.MEDECIN]: 'Médecin',
      [TypeUtilisateur.RECEPTIONNISTE]: 'Réceptionniste',
      [TypeUtilisateur.ADMIN]: 'Administrateur',
      [TypeUtilisateur.SUPER_ADMIN]: 'Super Admin',
      [TypeUtilisateur.MEDECIN_NOUVEAU]: 'Médecin Inactif',
    };
    return labels[type] || type;
  }

  getStatutLabel(statut: StatutUtilisateur): string {
    const labels = {
      [StatutUtilisateur.ACTIF]: 'Actif',
      [StatutUtilisateur.INACTIF]: 'Inactif',
      [StatutUtilisateur.SUSPENDU]: 'Suspendu',
      [StatutUtilisateur.SUPPRIME]: 'Supprimé'
    };
    return labels[statut] || statut;
  }

  private afficherMessage(message: string, type: 'success' | 'error' | 'warning') {
    this.message = message;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }
   supprimerUtilisateur(utilisateur: UtilisateurDetailDto) {
    this.userToDelete = utilisateur;
    this.showDeleteModal = true;
  }

  confirmerSuppression() {
    if (!this.userToDelete) return;
    
    this.loading = true;
    this.adminService.supprimerDefinitivement(this.userToDelete.id!)
      .subscribe({
        next: () => {
          this.afficherMessage('Utilisateur supprimé avec succès', 'success');
          this.chargerUtilisateurs();
          this.showDeleteModal = false;
          this.userToDelete = null;
        },
        error: (error) => {
          this.afficherMessage('Erreur lors de la suppression', 'error');
          this.loading = false;
        }
      });
  }

  annulerSuppression() {
    this.showDeleteModal = false;
    this.userToDelete = null;
  }
}