import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Hopital, Service, StatutHopital, StatutService } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { HopitalService } from '../../core/services/hopital.service';
import { ServiceService } from '../../core/services/service.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hopital-services',
  templateUrl: './hopital-services.component.html',
  styleUrls: ['./hopital-services.component.css'],
  imports: [CommonModule ]
})
export class HopitalServicesComponent implements OnInit {
  hopital: Hopital | null = null;
  services: Service[] = [];
  loading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private serviceService: ServiceService,
    private hopitalService: HopitalService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadHopitalAndServices(+id);
    }
  }

  loadHopitalAndServices(id: number): void {
    this.loading = true;
    
    // Charge les détails de l'hôpital
    this.hopitalService.getHopitalById(id).subscribe({
      next: (hopital) => {
        this.hopital = hopital;
        
        // Charge les services de l'hôpital
        this.serviceService.getServicesByHopital(id).subscribe({
          next: (services) => {
            this.services = services;
            this.loading = false;
          },
          error: (error) => {
            console.error('Erreur lors du chargement des services:', error);
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'hôpital:', error);
        this.loading = false;
      }
    });
  }

  getStatutColor(statut: StatutService | StatutHopital): string {
  switch (statut) {
    case StatutService.ACTIF:
    case StatutHopital.ACTIF: 
      return 'success';
    case StatutService.INACTIF:
    case StatutHopital.INACTIF: 
      return 'danger';
    case StatutService.MAINTENANCE:
    case StatutHopital.MAINTENANCE: 
      return 'warning';
    default: 
      return 'secondary';
  }
}
}