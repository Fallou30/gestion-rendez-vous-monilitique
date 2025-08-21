import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface ProfilUtilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  type: string;
  statut: string;
  dateNaissance: Date;
  lieuNaissance: string;
  sexe: string;
  adresse: string;
  telephone: string;
  dateCreation: Date;
  dateModification: Date;
  medecin?: {
    specialite: string;
    matricule: string;
    idHopitaux: number[];
    biographie: string;
    numeroOrdre: string;
    experience: number;
    idService: number;
    titre: string;
  };
  patient?: {
    profession: string;
    groupeSanguin: string;
    allergies: string;
    contactUrgenceNom: string;
    contactUrgenceTelephone: string;
    preferencesNotification: string;
  };
  receptionniste?: {
    poste: string;
    idHopital: number;
    idService: number;
  };
  admin?: {
    role: string;
    permissions: string;
  };
}

export interface ChangementMotDePasse {
  ancienMotDePasse: string;
  nouveauMotDePasse: string;
  confirmationMotDePasse: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfilService {
  private apiUrl = `http://localhost:8081/api/v1/profil`;

  constructor(private http: HttpClient) { }

  getProfil(userId: number): Observable<ProfilUtilisateur> {
    return this.http.get<ProfilUtilisateur>(`${this.apiUrl}/${userId}`);
  }

  updateProfil(userId: number, profil: ProfilUtilisateur): Observable<ProfilUtilisateur> {
    return this.http.put<ProfilUtilisateur>(`${this.apiUrl}/${userId}`, profil);
  }

  changerMotDePasse(userId: number, changement: ChangementMotDePasse): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}/changer-mot-de-passe`, changement);
  }

  // Méthode pour construire un objet ProfilUtilisateur vide
  createEmptyProfil(): ProfilUtilisateur {
    return {
      id: 0,
      nom: '',
      prenom: '',
      email: '',
      type: '',
      statut: '',
      dateNaissance: new Date(),
      lieuNaissance: '',
      sexe: '',
      adresse: '',
      telephone: '',
      dateCreation: new Date(),
      dateModification: new Date()
    };
  }
}