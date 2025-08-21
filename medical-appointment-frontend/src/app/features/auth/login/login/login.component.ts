import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { LoginRequestDto, TypeUtilisateur } from '../../../../core/models/utilisateur/utilisateur.module';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  returnUrl = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required]]
    });
  }

    ngOnInit(): void {
    const isLoggedIn = this.authService.isAuthenticated();

    if (isLoggedIn) {
      // Utilise le bon dashboard basé sur le rôle de l’utilisateur
      const dashboardRoute = this.authService.getDashboardRoute();
      console.log('Déjà authentifié, redirection vers:', dashboardRoute);
      this.router.navigateByUrl(dashboardRoute);
    } else {
      // L'utilisateur veut se connecter, récupère éventuellement returnUrl
      this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || null;
    }
  }


  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
  if (this.loginForm.valid) {
    this.isLoading = true;
    this.errorMessage = '';
    this.authService.login(this.loginForm.value).subscribe({
    next: (response) => {
      this.isLoading = false;
      
      // Si un returnUrl est fourni, y aller ; sinon, selon le rôle
      const redirectTo = this.returnUrl || this.getRouteForUserType(response.utilisateur.type!);
      this.router.navigateByUrl(redirectTo);
    },
    error: (error) => {
      this.isLoading = false;
      this.errorMessage = 'Identifiants incorrects';
      console.error('Login error:', error);
    }
  });

  }
}

private redirectBasedOnRole(userType: TypeUtilisateur): void {
  const targetRoute = this.getRouteForUserType(userType);
  console.log(`Redirecting ${userType} to ${targetRoute}`);
  
  this.router.navigateByUrl(targetRoute).then(success => {
    if (!success) {
      console.warn('Redirection failed, falling back to home');
      this.router.navigate(['/home']);
    }
  });
}

private getRouteForUserType(userType: TypeUtilisateur): string {
  const routes = {
    [TypeUtilisateur.SUPER_ADMIN]: '/super-admin/dashboard',
    [TypeUtilisateur.ADMIN]: '/admin/dashboard',
    [TypeUtilisateur.MEDECIN]: '/medecin/dashboard',
    [TypeUtilisateur.PATIENT]: '/patient/dashboard',
    [TypeUtilisateur.RECEPTIONNISTE]: '/receptionniste/dashboard',
    [TypeUtilisateur.MEDECIN_NOUVEAU]: '/demande/medecin'
  };

  return routes[userType] || '/home';
}
}