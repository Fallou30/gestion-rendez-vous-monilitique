/* // src/app/core/services/inscription.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  InscriptionPatientDto,
  DemandeMedecinDto,
  CreationMedecinDto,
  CreationAdminDto,
  InscriptionReceptionnisteDto,
  ModificationPatientDto,
  ModificationMedecinDto,
  ModificationAdminDto,
  ModificationProfilDto,
  ChangementMotDePasseDto,
  UtilisateurDto,
  UtilisateurDetailDto,
  PersonnelMedicalDto,
  ModificationAccesDto,
  AuthResponseDto,
  EmailDisponibiliteResponse,
  StatutUtilisateur,
  Medecin,
  Patient,
} from '../models//utilisateur/utilisateur.module';
import { AuthService } from './auth.service';
import { FileUploadResponse } from './file-upload.service';

@Injectable({
  providedIn: 'root',
})
export class InscriptionService {
  updateStatutPersonnel(id: number, nouveauStatut: StatutUtilisateur): Observable<any> {
    throw new Error('Method not implemented.');
  }
  private readonly API_URL = 'http://localhost:8081/api/v1';

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Inscriptions publiques
  inscrirePatient(dto: InscriptionPatientDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.API_URL}/public/patient/inscription`,
      dto
    );
  }

  demanderInscriptionMedecin(demande: FormData): Observable<any> {
    return this.http.post(`${this.API_URL}/medecin/demande`, demande);
  }
  listerDemandeInscriptionMedecin(): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(
      `${this.API_URL}/demandes/medecins`
    );
  }
  // Validation par admin
  validerDemandeInscription(
    idMedecin: number,
    approuver: boolean,
    commentaire?: string
  ): Observable<AuthResponseDto> {
    const params = new HttpParams()
      .set('approuver', approuver.toString())
      .set('commentaire', commentaire || '');

    return this.http.post<AuthResponseDto>(
      `${this.API_URL}/admin/valider-demande/${idMedecin}`,
      {},
      {
        params,
        headers: this.authService.getAuthenticatedHeaders(),
      }
    );
  }
  getUserById(id:number)  : Observable<UtilisateurDto> {
    return this.http.get<UtilisateurDto>(`${this.API_URL}/user/${id}`, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  } 
  //Pas encore fais
  // getMedecinsBySerice(id:number)  : Observable<> {
  //   return this.http.get<UtilisateurDto>(`${this.API_URL}/user/${id}`, {
  //     headers: this.authService.getAuthenticatedHeaders(),
  //   });
  // } 
  // Créations par admin
  creerMedecin(dto: CreationMedecinDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.API_URL}/admin/medecin/inscription`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  creerAdministrateur(dto: CreationAdminDto): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.API_URL}/admin/administrateur/inscription`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  inscrireReceptionniste(
    dto: InscriptionReceptionnisteDto
  ): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.API_URL}/admin/receptionnistes`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  // Vérification email
  verifierDisponibiliteEmail(
    email: string
  ): Observable<EmailDisponibiliteResponse> {
    return this.http.get<EmailDisponibiliteResponse>(
      `${this.API_URL}/public/verifier-email/${email}`
    );
  }

  // Modifications
  modifierPatient(
    id: number,
    dto: ModificationPatientDto
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.API_URL}/admin/patients/${id}`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  modifierMedecin(
    id: number,
    dto: ModificationMedecinDto
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.API_URL}/admin/medecins/${id}`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }
  uploadMedecinDocument(medecinId: number, file: File, type: string): Observable<FileUploadResponse> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', type);

  return this.http.post<FileUploadResponse>(`${this.API_URL}/files/medecins/${medecinId}/documents`, formData);
}

  modifierAdministrateur(
    id: number,
    dto: ModificationAdminDto
  ): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.API_URL}/admin/administrateurs/${id}`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  // Suppressions
  supprimerPatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/patients/${id}`, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  }

  supprimerMedecin(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/medecins/${id}`, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  }

  supprimerAdministrateur(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/admin/administrateurs/${id}`,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }

  // Listings
  listerUtilisateurs(
    type?: string,
    statut?: string
  ): Observable<UtilisateurDto[]> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);
    if (statut) params = params.set('statut', statut);

    return this.http.get<UtilisateurDto[]>(
      `${this.API_URL}/admin/utilisateurs`,
      {
        params,
        headers: this.authService.getAuthenticatedHeaders(),
      }
    );
  }

  listerPersonnelMedical(
    idHopital: number,
    type?: string
  ): Observable<PersonnelMedicalDto[]> {
    let params = new HttpParams();
    if (type) params = params.set('type', type);

    return this.http.get<PersonnelMedicalDto[]>(
      `${this.API_URL}/admin/hopital/${idHopital}`,
      {
        params,
        headers: this.authService.getAuthenticatedHeaders(),
      }
    );
  }

  // Gestion des accès
  modifierAccesPersonnel(
    id: number,
    dto: ModificationAccesDto
  ): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/admin/${id}/acces`, dto, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  }

  // Profil utilisateur
  consulterProfil(): Observable<UtilisateurDetailDto> {
    return this.http.get<UtilisateurDetailDto>(`${this.API_URL}/users/profil`, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  }

  modifierProfil(dto: ModificationProfilDto): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(`${this.API_URL}/users/profil`, dto, {
      headers: this.authService.getAuthenticatedHeaders(),
    });
  }
  getMedecinDetailById(medecinId: number): Observable<Medecin> {
    return this.http.get<Medecin>(`${this.API_URL}/medecin/${medecinId}`);
  }

  changerMotDePasse(dto: ChangementMotDePasseDto): Observable<void> {
    return this.http.put<void>(
      `${this.API_URL}/users/profil/changer-mot-de-passe`,
      dto,
      { headers: this.authService.getAuthenticatedHeaders() }
    );
  }
  getPatientById(id: number): Observable<Patient> {
  return this.http.get<Patient>(`${this.API_URL}/patient/${id}`);
  }
   getPatients(): Observable<Patient[]> {
  return this.http.get<Patient[]>(`${this.API_URL}/patients/`);
  }


}
 */