import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { StatutHopital } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { Hopital } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { HopitalService } from '../../core/services/hopital.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-hopital-list',
  templateUrl: './hopital-list.component.html',
  styleUrls: ['./hopital-list.component.css'],
  imports: [CommonModule,RouterModule,FormsModule]
})
export class HopitalListComponent implements OnInit {
  hopitaux: Hopital[] = [];
  filteredHopitaux: Hopital[] = [];
  searchTerm: string = '';
  selectedStatut: StatutHopital | '' = '';
  selectedVille: string = '';
  selectedRegion: string = '';
  loading: boolean = false;

  statutOptions = Object.values(StatutHopital);
  villes: string[] = [];
  regions: string[] = [];

  constructor(
    private hopitalService: HopitalService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHopitaux();
  }

  loadHopitaux(): void {
    this.loading = true;
    this.hopitalService.getAllHopitaux().subscribe({
      next: (data) => {
        this.hopitaux = data;
        this.filteredHopitaux = data;
        this.extractFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des hôpitaux:', error);
        this.loading = false;
      }
    });
  }

  extractFilters(): void {
    this.villes = [...new Set(this.hopitaux.map(h => h.ville).filter((v): v is string => typeof v === 'string'))];
    this.regions = [...new Set(this.hopitaux.map(h => h.region).filter((r): r is string => typeof r === 'string'))];
  }

  applyFilters(): void {
    this.filteredHopitaux = this.hopitaux.filter(hopital => {
      const matchesSearch = !this.searchTerm || 
        hopital.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (hopital.ville && hopital.ville.toLowerCase().includes(this.searchTerm.toLowerCase()));
      
      const matchesStatut = !this.selectedStatut || hopital.statut === this.selectedStatut;
      const matchesVille = !this.selectedVille || hopital.ville === this.selectedVille;
      const matchesRegion = !this.selectedRegion || hopital.region === this.selectedRegion;

      return matchesSearch && matchesStatut && matchesVille && matchesRegion;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatut = '';
    this.selectedVille = '';
    this.selectedRegion = '';
    this.filteredHopitaux = this.hopitaux;
  }

  viewHopital(id: number): void {
  this.router.navigate(['/hopitaux', id, 'services']);
}

  editHopital(id: number): void {
    this.router.navigate(['/editer/hopitaux', id]);
  }

  deleteHopital(hopital: Hopital): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'hôpital "${hopital.nom}" ?`)) {
      this.hopitalService.deleteHopital(hopital.idHopital!).subscribe({
        next: () => {
          this.loadHopitaux();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
        }
      });
    }
  }

  changeStatut(hopital: Hopital, nouveauStatut: StatutHopital): void {
    this.hopitalService.changeStatutHopital(hopital.idHopital!, nouveauStatut).subscribe({
      next: (hopitalMisAJour) => {
        const index = this.hopitaux.findIndex(h => h.idHopital === hopitalMisAJour.idHopital);
        if (index !== -1) {
          this.hopitaux[index] = hopitalMisAJour;
          this.applyFilters();
        }
      },
      error: (error) => {
        console.error('Erreur lors du changement de statut:', error);
      }
    });
  }

  getStatutColor(statut: StatutHopital): string {
    switch (statut) {
      case StatutHopital.ACTIF: return 'success';
      case StatutHopital.INACTIF: return 'danger';
      case StatutHopital.MAINTENANCE: return 'warning';
      default: return 'secondary';
    }
  }
}
