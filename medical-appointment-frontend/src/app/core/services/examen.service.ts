import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Examen {
  id: number;
  consultationId: number;
  typeExamen: string;
  nomExamen: string;
  description: string;
  urgence: boolean;
  dateRealisation: Date;
  resultats: string;
  interpretation: string;
  statut: string;
}

export interface CreateExamenRequest {
  consultationId: number;
  typeExamen: string;
  nomExamen: string;
  description: string;
  urgence: boolean;
}

export interface ExamenResultatsRequest {
  resultats: string;
  interpretation: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExamenService {
  private apiUrl = 'http://localhost:8081/api/v1/examens';

  constructor(private http: HttpClient) {}

  prescrireExamen(request: CreateExamenRequest): Observable<Examen> {
    return this.http.post<Examen>(this.apiUrl, request);
  }

  programmerExamen(id: number, dateRealisation: string): Observable<Examen> {
    const params = new HttpParams().set('dateRealisation', dateRealisation);
    return this.http.put<Examen>(`${this.apiUrl}/${id}/programmer`, null, { params });
  }

  saisirResultats(id: number, request: ExamenResultatsRequest): Observable<Examen> {
    return this.http.put<Examen>(`${this.apiUrl}/${id}/resultats`, request);
  }

  getExamensPatient(patientId: number): Observable<Examen[]> {
    return this.http.get<Examen[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  getExamensEnAttente(): Observable<Examen[]> {
    return this.http.get<Examen[]>(`${this.apiUrl}/en-attente`);
  }

  getExamensUrgents(): Observable<Examen[]> {
    return this.http.get<Examen[]>(`${this.apiUrl}/urgents`);
  }

  getExamenById(id: number): Observable<Examen> {
    return this.http.get<Examen>(`${this.apiUrl}/${id}`);
  }
}