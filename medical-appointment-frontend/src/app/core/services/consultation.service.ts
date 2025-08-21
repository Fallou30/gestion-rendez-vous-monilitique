import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Consultation {
  id: number;
  rendezVousId: number;
  symptomes: string;
  diagnostic: string;
  observations: string;
  recommandations: string;
  dateConsultation: Date;
  dureeReelle: number;
  statut: string;
}

export interface CreateConsultationRequest {
  rendezVousId: number;
  symptomes: string;
  diagnostic: string;
  observations: string;
  recommandations: string;
}

export interface UpdateConsultationRequest {
  symptomes: string;
  diagnostic: string;
  observations: string;
  recommandations: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConsultationService {
  private apiUrl = 'http://localhost:8081/api/v1/consultations';

  constructor(private http: HttpClient) {}

  creerConsultation(request: CreateConsultationRequest): Observable<Consultation> {
    return this.http.post<Consultation>(this.apiUrl, request);
  }

  mettreAJourConsultation(id: number, request: UpdateConsultationRequest): Observable<Consultation> {
    return this.http.put<Consultation>(`${this.apiUrl}/${id}`, request);
  }

  terminerConsultation(id: number, dureeReelle: number): Observable<Consultation> {
    const params = new HttpParams().set('dureeReelle', dureeReelle.toString());
    return this.http.put<Consultation>(`${this.apiUrl}/${id}/terminer`, null, { params });
  }

  getHistoriqueConsultations(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(`${this.apiUrl}/patient/${patientId}/historique`);
  }

  getConsultationsMedecin(medecinId: number, dateDebut: string, dateFin: string): Observable<Consultation[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get<Consultation[]>(`${this.apiUrl}/medecin/${medecinId}`, { params });
  }

  getConsultationById(id: number): Observable<Consultation> {
    return this.http.get<Consultation>(`${this.apiUrl}/${id}`);
  }
}