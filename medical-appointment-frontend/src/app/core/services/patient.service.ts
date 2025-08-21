import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Utilisateur, InscriptionPatient, AuthResponseDto, UtilisateurDetailDto } from '../models/utilisateur/utilisateur.module';


// Interface pour le DTO de modification de profil patient
export interface ModificationProfilPatientDto extends Utilisateur {
  groupeSanguin?: string;
  allergies?: string;
  contactUrgenceNom?: string;
  contactUrgenceTelephone?: string;
}

// Interface pour le changement de mot de passe
export interface ChangementMotDePasseDto {
  ancienMotDePasse: string;
  nouveauMotDePasse: string;
  confirmationNouveauMotDePasse: string;
}

// Interface pour le PatientDto
export interface PatientDto extends Utilisateur {
  numAssurance?: string;
  groupeSanguin?: string;
  allergies?: string;
  contactUrgenceNom?: string;
  contactUrgenceTelephone?: string;
  profession?: string;
  preferencesNotification?: string;
  idDossierMedical?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly apiUrl = 'http://localhost:8081/api/v1';

  constructor(private http: HttpClient) {}

  /**
   * Inscription d'un nouveau patient
   * @param inscriptionData - Données d'inscription du patient
   * @returns Observable<AuthResponseDto>
   */
  inscrirePatient(inscriptionData: InscriptionPatient): Observable<AuthResponseDto> {
    return this.http.post<AuthResponseDto>(
      `${this.apiUrl}/public/patient/inscription`, 
      inscriptionData
    );
  }

  /**
   * Consulter le profil d'un patient par email
   * @param email - Email du patient
   * @returns Observable<UtilisateurDetailDto>
   */
  consulterProfil(email: string): Observable<UtilisateurDetailDto> {
    const params = new HttpParams().set('email', email);
    return this.http.get<UtilisateurDetailDto>(
      `${this.apiUrl}/patient/profil`, 
      { params }
    );
  }

  /**
   * Modifier le profil d'un patient
   * @param id - ID du patient
   * @param profilData - Données de modification du profil
   * @returns Observable<AuthResponseDto>
   */
  modifierProfil(id: number, profilData: ModificationProfilPatientDto): Observable<AuthResponseDto> {
    return this.http.put<AuthResponseDto>(
      `${this.apiUrl}/patient/${id}/modifier-profil`, 
      profilData
    );
  }

  /**
   * Changer le mot de passe d'un patient
   * @param id - ID du patient
   * @param motDePasseData - Données de changement de mot de passe
   * @returns Observable<void>
   */
  changerMotDePasse(id: number, motDePasseData: ChangementMotDePasseDto): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/patient/${id}/changer-mot-de-passe`, 
      motDePasseData
    );
  }

  /**
   * Récupérer un patient par son ID
   * @param id - ID du patient
   * @returns Observable<PatientDto>
   */
  getPatientById(id: number): Observable<PatientDto> {
    return this.http.get<PatientDto>(`${this.apiUrl}/patient/${id}`);
  }
  /**
   * Récupérer tous les patients
   * @returns Observable<PatientDto[]>
   */
  getPatients(): Observable<PatientDto[]> {
    return this.http.get<PatientDto[]>(`${this.apiUrl}/patient/all`);
  }

  /**
   * Vérifier si un email existe déjà
   * @param email - Email à vérifier
   * @returns Observable<boolean>
   */
  emailExiste(email: string): Observable<boolean> {
    const params = new HttpParams().set('email', email);
    return this.http.get<boolean>(
      `${this.apiUrl}/public/patient/email-existe`, 
      { params }
    );
  }

  // Méthodes utilitaires additionnelles

  /**
   * Valider les données d'inscription avant envoi
   * @param data - Données d'inscription
   * @returns boolean
   */
  validerDonneesInscription(data: InscriptionPatient): boolean {
    return !!(
      data.nom?.trim() &&
      data.prenom?.trim() &&
      data.email?.trim() &&
      data.motDePasse &&
      data.confirmationMotDePasse &&
      data.motDePasse === data.confirmationMotDePasse &&
      data.dateNaissance &&
      data.telephone?.trim() &&
      data.profession?.trim() &&
      data.contactUrgenceNom?.trim() &&
      data.contactUrgenceTelephone?.trim()
    );
  }

  /**
   * Valider les données de modification de profil
   * @param data - Données de modification
   * @returns boolean
   */
  validerDonneesModification(data: ModificationProfilPatientDto): boolean {
    return !!(
      data.nom?.trim() &&
      data.prenom?.trim() &&
      data.email?.trim() &&
      data.telephone?.trim()
    );
  }

  /**
   * Valider le format de l'email
   * @param email - Email à valider
   * @returns boolean
   */
  validerFormatEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Valider le format du téléphone
   * @param telephone - Numéro de téléphone à valider
   * @returns boolean
   */
  validerFormatTelephone(telephone: string): boolean {
    // Regex pour format sénégalais (exemple: +221771234567, 771234567)
    const telephoneRegex = /^(\+221)?[0-9]{9}$/;
    return telephoneRegex.test(telephone.replace(/\s/g, ''));
  }

  /**
   * Formater le numéro de téléphone
   * @param telephone - Numéro à formater
   * @returns string
   */
  formaterTelephone(telephone: string): string {
    const cleaned = telephone.replace(/\D/g, '');
    if (cleaned.startsWith('221')) {
      return `+${cleaned}`;
    } else if (cleaned.length === 9) {
      return `+221${cleaned}`;
    }
    return telephone;
  }
}