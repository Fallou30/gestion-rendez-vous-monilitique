import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FileUploadService } from '../../core/services/file-upload.service';
import { Medecin, StatutUtilisateur } from '../../core/models/utilisateur/utilisateur.module';
import { HttpErrorResponse } from '@angular/common/http';
import { HopitalService } from '../../core/services/hopital.service';
import { AdminService } from '../../core/services/admin.service';



@Component({
  selector: 'app-validation-demandes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './validation-demandes.component.html',
  styleUrls: ['./validation-demandes.component.scss']
})
export class ValidationDemandesComponent implements OnInit {
  demandes: Medecin[] = [];
  demandesFiltrees: Medecin[] = [];
  demandeSelectionnee: Medecin | null = null;
  specialites: string[] = [];
  hopitauxMap: Map<number, string> = new Map();
   
  // Filtres
  filtreStatut: StatutUtilisateur | '' = '';
  filtreSpecialite = '';
  rechercheText = '';
  motifDemande: string = '';
  // Modal de rejet
  showRejetModal = false;
  motifRejet = '';
  demandeARejetee: Medecin | null = null;
  motifsDisponibles: string[] = [
  'Complément de dossier requis',
  'Manque de pièces justificatives',
  'Profil incomplet',
  'Spécialité non reconnue',
  'Incohérence dans les informations fournies',
  'Demande en doublon',
  'Autre'
 ];


  constructor(
    private adminService: AdminService,
    private fileUploadService: FileUploadService,
    private router: Router,
    private route: ActivatedRoute,
    private hopitalService: HopitalService,
  ) {}

  ngOnInit() {
    this.chargerDemandes();
    this.checkRouteParams();
    this.chargerHopitaux();
  }

  checkRouteParams() {
    this.route.queryParams.subscribe(params => {
      if (params['userId'] && params['action']) {
        const userId = Number(params['userId']);
        const action = params['action'];
        
        const demande = this.demandes.find(d => d.id === userId);
        if (demande) {
          if (action === 'approve') {
            this.approuver(demande);
          } else if (action === 'reject') {
            this.rejeter(demande);
          }
        }
      }
    });
  }
 chargerHopitaux() {
  this.hopitalService.getAllHopitaux().subscribe({
    next: (hopitaux) => {
      
      hopitaux.forEach(h => {
   if (h.idHopital !== undefined) {
    this.hopitauxMap.set(h.idHopital, h.nom);
    }
    });;
    },
    error: (err) => console.error('Erreur chargement hôpitaux', err)
  });
}
getNomHopital(ids: number[] | number | undefined): string {
  if (Array.isArray(ids)) {
    return ids.map(id => this.hopitauxMap.get(id) || 'Inconnu').join(', ');
  }
  return ids && this.hopitauxMap.has(ids) ? this.hopitauxMap.get(ids)! : 'Inconnu';
}

  chargerDemandes() {
    this.adminService.listerDemandesValidation().subscribe({
      next: (demandes: Medecin[]) => {
        this.demandes = demandes;
        this.demandesFiltrees = demandes;
        this.extractSpecialites();
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des demandes:', error);
      }
    });
  }

  extractSpecialites() {
    this.specialites = [...new Set(this.demandes.map(d => d.specialite || ''))].filter(s => s !== '');
  }

  filtrerDemandes() {
    this.demandesFiltrees = this.demandes.filter(demande => {
      const matchStatut = !this.filtreStatut || demande.statut === this.filtreStatut;
      const matchSpecialite = !this.filtreSpecialite || demande.specialite === this.filtreSpecialite;
      const matchRecherche = !this.rechercheText || 
        demande.nom.toLowerCase().includes(this.rechercheText.toLowerCase()) ||
        demande.prenom.toLowerCase().includes(this.rechercheText.toLowerCase()) ||
        (demande.email && demande.email.toLowerCase().includes(this.rechercheText.toLowerCase()));
      
      return matchStatut && matchSpecialite && matchRecherche;
    });
  }

  voirDetails(demande: Medecin) {
    this.demandeSelectionnee = demande;
  }

  fermerModal() {
    this.demandeSelectionnee = null;
  }

  approuver(demande: Medecin) {
    if (!demande.id) {
      console.error('ID du médecin non défini');
      return;
    }

    this.adminService.validerDemandeInscription(demande.id, true,this.motifDemande).subscribe({
      next: () => {
        demande.statut = StatutUtilisateur.ACTIF;
        this.fermerModal();
        alert('Médecin activé avec succès');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors de l\'activation:', error);
        alert('Erreur lors de l\'activation');
      }
    });
  }

  rejeter(demande: Medecin) {
    this.demandeARejetee = demande;
    this.showRejetModal = true;
  }

  confirmerRejet() {
    if (!this.demandeARejetee?.id) {
      console.error('ID du médecin non défini');
      return;
    }

    this.adminService.validerDemandeInscription(
      this.demandeARejetee.id, 
      false, 
      this.motifRejet
    ).subscribe({
      next: () => {
        if (this.demandeARejetee) {
          this.demandeARejetee.statut = StatutUtilisateur.SUSPENDU;
        }
        this.annulerRejet();
        this.fermerModal();
        alert('Médecin suspendu avec succès');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors de la suspension:', error);
        alert('Erreur lors de la suspension');
      }
    });
  }

  annulerRejet() {
    this.showRejetModal = false;
    this.motifRejet = this.motifDemande;
    this.demandeARejetee = null;
  }

  previewFile(filePath: string) {
    const previewUrl = this.fileUploadService.getFilePreviewUrl(filePath);
    window.open(previewUrl, '_blank');
  }

  downloadFile(filePath: string) {
    this.fileUploadService.downloadFile(filePath).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filePath.split('/').pop() || 'document';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors du téléchargement:', error);
        alert('Erreur lors du téléchargement');
      }
    });
  }

  getStatutLabel(statut: StatutUtilisateur): string {
    switch (statut) {
      case 'INACTIF': return 'Inactif';
      case 'ACTIF': return 'Actif';
      case 'SUSPENDU': return 'Suspendu';
      default: return statut;
    }
  }

  getStatusClass(statut: StatutUtilisateur): string {
    switch (statut) {
      case 'INACTIF': return 'status-inactive';
      case 'ACTIF': return 'status-active';
      case 'SUSPENDU': return 'status-suspended';
      default: return '';
    }
  }
}