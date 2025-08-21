// src/app/pages/unauthorized/unauthorized.component.ts
import { Component } from '@angular/core';
import { Router, NavigationExtras } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TypeUtilisateur } from '../../core/models/utilisateur/utilisateur.module';
import { CommonModule } from '@angular/common';
import { DashboardNavComponent } from '../../core/components/dashboard-nav/dashboard-nav.component';

@Component({
  selector: 'app-unauthorized',
  templateUrl: './unauthorized.component.html',
  styleUrls: ['./unauthorized.component.scss'],
  imports: [DashboardNavComponent,CommonModule]
})
export class UnauthorizedComponent {
  currentUserRole: string = 'Non connecté';
  attemptedRoute: string = 'cette ressource';
  requiredRoles: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // Récupération des données de navigation
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as {
      route?: string;
      requiredRoles?: TypeUtilisateur[];
    };

    // Initialisation des propriétés
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        if (user.type !== undefined) {
          this.currentUserRole = this.getRoleLabel(user.type);
        }
      }
    });

    if (state?.route) {
      this.attemptedRoute = state.route;
    }

    if (state?.requiredRoles) {
      this.requiredRoles = state.requiredRoles.map(role => this.getRoleLabel(role));
    }
  }

  private getRoleLabel(role: TypeUtilisateur): string {
    const roleLabels: Record<TypeUtilisateur, string> = {
      [TypeUtilisateur.PATIENT]: 'Patient',
      [TypeUtilisateur.MEDECIN]: 'Médecin',
      [TypeUtilisateur.RECEPTIONNISTE]: 'Réceptionniste',
      [TypeUtilisateur.ADMIN]: 'Administrateur',
      [TypeUtilisateur.SUPER_ADMIN]: 'Super Admin',
      [TypeUtilisateur.MEDECIN_NOUVEAU]: 'Médecin',
    
    };
    return roleLabels[role] || role;
  }

  redirectToHome(): void {
    this.router.navigate(['/']);
  }

  redirectToLogin(): void {
    this.router.navigate(['/auth/login'], {
      queryParams: { returnUrl: this.attemptedRoute }
    });
  }
}