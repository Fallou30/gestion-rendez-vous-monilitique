import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreationMedecinDto,
  AuthResponseDto,
  NouveauMedecinDto,
  ModificationProfilMedecinDto,
  Utilisateur,
  TypeUtilisateur,
  StatutUtilisateur,
  UtilisateurDetailDto,
  Receptionniste,
  ModificationPatient,
  Medecin,
  ModificationAdminDto,
  ModificationMedecinDto,
} from '../models/utilisateur/utilisateur.module';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private baseUrl = 'http://localhost:8081/api/v1/admin'; // Assure-toi que ça correspond à l'URL de ton proxy ou backend

  constructor(private http: HttpClient) {}

  // ==========================
  // MÉDECINS
  // ==========================

  creerMedecin(dto: CreationMedecinDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(`${this.baseUrl}/medecins`, dto);
  }

  creerNouveauMedecin(dto: NouveauMedecinDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.baseUrl}/medecins/nouveau`,
      dto
    );
  }

  modifierMedecin(
    id: number,
    dto: ModificationMedecinDto
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.baseUrl}/medecins/${id}`,
      dto
    );
  }

  supprimerMedecin(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/medecins/${id}`);
  }

  // ==========================
  // VALIDATION DEMANDES
  // ==========================

  listerDemandesValidation(): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(
      `${this.baseUrl}/medecins/demandes-validation`
    );
  }

  validerDemandeInscription(
    id: number,
    approuver: boolean,
    commentaire: string = ''
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.baseUrl}/medecins/${id}/validation`,
      { approuver, commentaire }
    );
  }
  // Méthode conservée car utile pour changer le statut
  changerStatutUtilisateur(
    userId: number,
    statut: StatutUtilisateur
  ): Observable<UtilisateurDetailDto> {
    return this.http.put<UtilisateurDetailDto>(
      `${this.baseUrl}/utilisateurs/${userId}/statut`,
      { statut }
    );
  }
  modifierAdministrateur(
    id: number,
    data: ModificationAdminDto
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(`${this.baseUrl}/admins/${id}`, data);
  }

  // ==========================
  // PATIENTS
  // ==========================

  modifierPatient(
    id: number,
    dto: ModificationPatient
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.baseUrl}/patients/${id}`,
      dto
    );
  }

  supprimerPatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/patients/${id}`);
  }

  // ==========================
  // RÉCEPTIONNISTES
  // ==========================

  inscrireReceptionniste(dto: Receptionniste): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.baseUrl}/receptionistes`,
      dto
    );
  }

  modifierReceptionniste(
    id: number,
    dto: Receptionniste
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.baseUrl}/receptionistes/${id}`,
      dto
    );
  }

  supprimerReceptionniste(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/receptionistes/${id}`);
  }

  // ==========================
  // UTILISATEURS
  // ==========================

  listerUtilisateurs(
    type?: TypeUtilisateur,
    statut?: StatutUtilisateur
  ): Observable<UtilisateurDetailDto[]> {
    let params: any = {};
    if (type) params.type = type;
    if (statut) params.statut = statut;

    return this.http.get<UtilisateurDetailDto[]>(
      `${this.baseUrl}/utilisateurs`,
      { params }
    );
  }

  supprimerDefinitivement(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/utilisateurs/${id}/definitif`
    );
  }

  // ==========================
  // STATISTIQUES
  // ==========================

  obtenirStatistiques(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/statistiques`);
  }
}
