import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


@Injectable({
  providedIn: 'root'
})
export class AdminGuard {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }

    this.router.navigate(['/unauthorized']);
    return false;
  }
}
export const roleGuard: CanActivateFn = (route, state) => {
  return true;
};
