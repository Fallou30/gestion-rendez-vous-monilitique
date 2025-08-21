import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DossierMedical {
  id: number;
  patientId: number;
  antecedentsMedicaux: string;
  antecedentsFamiliaux: string;
  vaccinations: string;
  notesGenerales: string;
  dateCreation: Date;
  dateModification: Date;
  archive: boolean;
}

export interface DocumentMedical {
  id: number;
  typeDocument: string;
  nomFichier: string;
  cheminFichier: string;
  tailleFichier: number;
  description: string;
  dateAjout: Date;
}

export interface CreateDossierRequest {
  patientId: number;
  antecedentsMedicaux: string;
  antecedentsFamiliaux: string;
  vaccinations: string;
}

export interface UpdateDossierRequest {
  antecedentsMedicaux: string;
  antecedentsFamiliaux: string;
  vaccinations: string;
  notesGenerales: string;
}

@Injectable({
  providedIn: 'root'
})
export class DossierMedicalService {
  private apiUrl = 'http://localhost:8081/api/v1/dossiers-medicaux';

  constructor(private http: HttpClient) {}

  creerDossier(request: CreateDossierRequest): Observable<DossierMedical> {
    return this.http.post<DossierMedical>(this.apiUrl, request);
  }
  
  mettreAJourDossier(id: number, request: UpdateDossierRequest): Observable<DossierMedical> {
    return this.http.put<DossierMedical>(`${this.apiUrl}/${id}`, request);
  }

  getDossierPatient(patientId: number): Observable<DossierMedical> {
    return this.http.get<DossierMedical>(`${this.apiUrl}/patient/${patientId}`);
  }

  ajouterDocument(dossierId: number, file: File, typeDocument: string, description: string): Observable<DocumentMedical> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('typeDocument', typeDocument);
    formData.append('description', description);

    return this.http.post<DocumentMedical>(`${this.apiUrl}/${dossierId}/documents`, formData);
  }

  archiverDossier(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/archiver`, {});
  }
    // Dans dossier-medical.service.ts
  getDocumentsDossier(dossierId: number): Observable<DocumentMedical[]> {
    return this.http.get<DocumentMedical[]>(`${this.apiUrl}/dossiers/${dossierId}/documents`);
  }
}