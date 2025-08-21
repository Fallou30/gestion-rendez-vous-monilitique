import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { TypeUtilisateur } from '../models/utilisateur/utilisateur.module';


@Injectable({
  providedIn: 'root'
})
export class RoleGuard {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    
    const requiredRoles = route.data['roles'] as TypeUtilisateur[];
    
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    if (this.authService.hasAnyRole(requiredRoles)) {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
    
  }
}
