import { Component } from '@angular/core';
import { TypeUtilisateur } from '../../core/models/utilisateur/utilisateur.module';
import { DashboardNavComponent } from '../../core/components/dashboard-nav/dashboard-nav.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-receptionniste-dashboard',
  templateUrl: './receptionniste-dashboard.component.html',
  imports: [DashboardNavComponent, CommonModule],
})
export class ReceptionnisteDashboardComponent {
  userType = TypeUtilisateur.RECEPTIONNISTE;

  todaysStats = {
    totalAppointments: 24,
    completed: 18,
    pending: 6,
  };

  recentPatients = [
    { name: 'Pauline Roy', arrivalTime: '09:15', status: 'En attente' },
    { name: 'Marc Vidal', arrivalTime: '09:30', status: 'En consultation' },
  ];
}
