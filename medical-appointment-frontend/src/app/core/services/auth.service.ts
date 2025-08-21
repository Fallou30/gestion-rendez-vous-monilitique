// src/app/core/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { 
  LoginRequestDto, 
  AuthResponseDto, 
  Utilisateur, 
  TypeUtilisateur 
} from '../models/utilisateur/utilisateur.module';
// import { ChangementMotDePasseDto } from './patient.service'; // Cette importation semble incorrecte ici.
                                                             // ChangementMotDePasseDto devrait être dans un dossier partagé ou dans auth.service.

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8081/api/v1';
  // BehaviorSubject pour l'état de l'utilisateur et du token
  // Initialisés à null car l'utilisateur n'est pas connecté par défaut.
  private currentUserSubject = new BehaviorSubject<Utilisateur | null>(null);
  private tokenSubject = new BehaviorSubject<string | null>(null);

  // Observables publics pour que les composants puissent s'abonner aux changements d'état.
  public currentUser$ = this.currentUserSubject.asObservable();
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {
    // Appelé à l'initialisation du service pour restaurer l'état depuis localStorage.
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('currentUser');
    
    // Si un token ET un utilisateur sont trouvés dans localStorage, restaurer l'état.
    if (token && user) {
      try {
        this.tokenSubject.next(token);
        this.currentUserSubject.next(JSON.parse(user));
      } catch (e) {
        console.error("Erreur lors de la lecture de 'currentUser' depuis localStorage", e);
        // En cas d'erreur de parsing, nettoyer les données corrompues.
        this.logout(); 
      }
    }
  }

  // Permet de récupérer la valeur actuelle de l'utilisateur (utile pour les accès synchrones).
  get currentUserValue(): Utilisateur | null {
    return this.currentUserSubject.value;
  }

  // Permet de récupérer la valeur actuelle du token.
  get tokenValue(): string | null {
    return this.tokenSubject.value;
  }

  /**
   * Retourne l'ID de l'utilisateur actuellement connecté.
   * Cette méthode DOIT être appelée SEULEMENT APRES s'être assuré que l'utilisateur est authentifié
   * (via `isAuthenticated()` ou un AuthGuard).
   * @returns L'ID de l'utilisateur.
   * @throws Error si aucun utilisateur n'est connecté ou si l'ID est manquant.
   */
  getCurrentUserId(): number {
    const user = this.currentUserValue;
    if (!user || !user.id) {
      // Cette erreur est levée si la méthode est appelée sans utilisateur connecté.
      // Un AuthGuard devrait prévenir cela pour les routes protégées.
      throw new Error('Aucun utilisateur connecté ou ID utilisateur manquant');
    }
    return user.id;
  }

  /**
   * Tente de connecter l'utilisateur.
   * En cas de succès, stocke le token et les infos utilisateur dans localStorage et met à jour les BehaviorSubjects.
   * @param credentials Les identifiants de connexion.
   * @returns Un Observable contenant la réponse d'authentification.
   */
  login(credentials: LoginRequestDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(`${this.API_URL}/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response.token && response.utilisateur) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('currentUser', JSON.stringify(response.utilisateur));
            this.tokenSubject.next(response.token);
            this.currentUserSubject.next(response.utilisateur);
          } else {
            // Gérer le cas où la réponse ne contient pas ce qui est attendu
            console.warn("La réponse de connexion ne contient pas le token ou l'utilisateur complet.");
            this.logout(); // S'assurer de nettoyer l'état si la réponse est incomplète.
          }
        })
      );
  }

  /**
   * Récupère le token actuel (synchrone).
   * @returns Le token JWT ou null.
   */
  getToken(): string | null {
    return this.tokenValue;
  }

  /**
   * Déconnecte l'utilisateur en supprimant les données de session.
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.tokenSubject.next(null);
    this.currentUserSubject.next(null);
    // Optionnel: rediriger vers la page de login après déconnexion
    // this.router.navigate(['/auth/login']); 
  }

  /**
   * Vérifie si un utilisateur est actuellement authentifié.
   * Basé uniquement sur la présence d'un token en mémoire (initialisé depuis localStorage).
   * Pour une vérification plus robuste (expiration du token), il faudrait décoder le token.
   * @returns True si un token est présent, false sinon.
   */
  isAuthenticated(): boolean {
    return !!this.tokenValue;
  }

  /**
   * Vérifie si l'utilisateur connecté a un rôle spécifique.
   * @param role Le rôle à vérifier.
   * @returns True si l'utilisateur a le rôle spécifié, false sinon.
   */
  hasRole(role: TypeUtilisateur): boolean {
    const user = this.currentUserValue;
    return user?.type === role;
  }

  /**
   * Vérifie si l'utilisateur connecté a l'un des rôles spécifiés.
   * @param roles Un tableau de rôles à vérifier.
   * @returns True si l'utilisateur a au moins un des rôles, false sinon.
   */
  hasAnyRole(roles: TypeUtilisateur[]): boolean {
    const user = this.currentUserValue;
    return user ? roles.includes(user.type!) : false;
  }

  isAdmin(): boolean {
    return this.hasAnyRole([TypeUtilisateur.ADMIN, TypeUtilisateur.SUPER_ADMIN]);
  }

  isMedecin(): boolean {
    return this.hasRole(TypeUtilisateur.MEDECIN);
  }

  isPatient(): boolean {
    return this.hasRole(TypeUtilisateur.PATIENT);
  }
  
  isMedecin_new(): boolean {
    return this.hasRole(TypeUtilisateur.MEDECIN_NOUVEAU);
  }

  isReceptionniste(): boolean {
    return this.hasRole(TypeUtilisateur.RECEPTIONNISTE);
  }

  /**
   * Construit les en-têtes HTTP avec le token d'autorisation.
   * @returns HttpHeaders avec le token Bearer.
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.tokenValue;
    return new HttpHeaders({
      'Authorization': token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    });
  }

  /**
   * Méthode publique pour obtenir les en-têtes authentifiés.
   * @returns HttpHeaders.
   */
  getAuthenticatedHeaders(): HttpHeaders {
    return this.getAuthHeaders();
  }

  /**
   * Détermine la route du tableau de bord en fonction du type d'utilisateur.
   * @returns La route du tableau de bord.
   */
  getDashboardRoute(): string {
    const user = this.currentUserValue;
    switch (user?.type) {
      case TypeUtilisateur.SUPER_ADMIN:
        return '/super_admin/dashboard';
      case TypeUtilisateur.ADMIN:
        return '/admin/dashboard';
      case TypeUtilisateur.MEDECIN:
        return '/medecin/dashboard';
      case TypeUtilisateur.PATIENT:
        return '/patient/dashboard';
      case TypeUtilisateur.RECEPTIONNISTE:
        return '/receptionniste/dashboard';
      case TypeUtilisateur.MEDECIN_NOUVEAU:
        return '/medecin_nouveau/dashboard';
      default:
        // Si aucun utilisateur ou type non reconnu, rediriger vers la page de login
        return '/auth/login'; 
    }
  }
}