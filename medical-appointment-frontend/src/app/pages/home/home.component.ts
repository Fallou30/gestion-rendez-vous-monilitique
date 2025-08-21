import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
// Importez votre service d'authentification
import { MatSnackBar } from '@angular/material/snack-bar'; // Pour afficher des messages
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [CommonModule]
})
export class HomeComponent {
  
  constructor(
    private router: Router,
    private authService: AuthService, // Service pour vérifier si l'utilisateur est connecté
    private snackBar: MatSnackBar // Pour afficher des messages temporaires
  ) {}

  features = [
    {
      icon: 'calendar',
      title: 'Prise de rendez-vous',
      description: 'Réservez facilement vos consultations médicales en ligne'
    },
    {
      icon: 'user-doctor',
      title: 'Médecins qualifiés',
      description: 'Accédez à notre réseau de professionnels de santé'
    },
    {
      icon: 'clock',
      title: 'Disponibilité 24/7',
      description: 'Consultez vos rendez-vous à tout moment'
    },
    {
      icon: 'shield-check',
      title: 'Sécurisé',
      description: 'Vos données médicales sont protégées et confidentielles'
    }
  ];

  stats = [
    { number: '5000+', label: 'Patients satisfaits' },
    { number: '200+', label: 'Médecins partenaires' },
    { number: '50+', label: 'Spécialités médicales' },
    { number: '24/7', label: 'Support disponible' }
  ];

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }

  goToRegister() {
    this.router.navigate(['/auth/register/patient']);
  }

  bookAppointment() {
    // Vérifier si l'utilisateur est connecté
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/prendre/rendez-vous']);
    } else {
      // Afficher un message d'information
      this.snackBar.open('Veuillez créer un compte pour prendre un rendez-vous', 'OK', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
      
      // Rediriger vers la page d'inscription
      this.router.navigate(['/auth/register/patient']);
    }
  }

  viewSpecialties() {
    this.router.navigate(['/specialties']);
  }
}