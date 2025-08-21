import { Component } from '@angular/core';
import { TypeUtilisateur } from '../../core/models/utilisateur/utilisateur.module';
import { DashboardNavComponent } from '../../core/components/dashboard-nav/dashboard-nav.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-super-admin-dashboard',
  templateUrl: './super-admin-dashboard.component.html',
  imports: [CommonModule],
})
export class SuperAdminDashboardComponent {
  userType = TypeUtilisateur.SUPER_ADMIN;

  globalStats = {
    totalHospitals: 12,
    activeUsers: 843,
    systemHealth: 'Excellent',
  };

  criticalIssues = [
    {
      id: '#1254',
      description: 'Problème de synchronisation BD',
      priority: 'Haute',
    },
    {
      id: '#1251',
      description: 'Mise à jour sécurité requise',
      priority: 'Critique',
    },
  ];
}
