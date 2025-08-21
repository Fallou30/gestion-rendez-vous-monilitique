// src/app/components/service/service-list/service-list.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Service } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { ServiceService } from '../../core/services/service.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-service-list',
  templateUrl: './service-list.component.html',
  styleUrls: ['./service-list.component.scss'],
  imports: [CommonModule],
})
export class ServiceListComponent implements OnInit {
  services: Service[] = [];
  loading = false;
  hopitalId?: number;

  constructor(
    private serviceService: ServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params['hopitalId']) {
        this.hopitalId = +params['hopitalId'];
        this.loadServicesByHopital(this.hopitalId);
      } else {
        this.loadAllServices();
      }
    });
  }

  loadAllServices(): void {
    this.loading = true;
    this.serviceService.getAllServices().subscribe({
      next: (data) => {
        this.services = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      },
    });
  }

  loadServicesByHopital(idHopital: number): void {
    this.loading = true;
    this.serviceService.getServicesByHopital(idHopital).subscribe({
      next: (data) => {
        this.services = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      },
    });
  }

  editService(id: number): void {
    this.router.navigate(['/services/edit', id]);
  }

  addService(): void {
    this.router.navigate(['/services/create']);
  }

  deleteService(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce service ?')) {
      this.serviceService.deleteService(id).subscribe({
        next: () => this.loadServicesByHopital(this.hopitalId!),
        error: (err) => console.error(err),
      });
    }
  }
}
