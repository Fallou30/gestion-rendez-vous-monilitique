import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Prescription {
  id: number;
  consultationId: number;
  dureeTraitement: number;
  instructionsGenerales: string;
  statut: string;
  dateCreation: Date;
  dateTerminaison: Date;
  medicaments: MedicamentPrescrit[];
}

export interface MedicamentPrescrit {
  id: number;
  nomMedicament: string;
  dosage: string;
  frequence: string;
  duree: number;
  instructionsSpecifiques: string;
  quantitePrescrite: number;
  statut: StatutMedicament;
}

export enum StatutMedicament {
  PRESCRIT = 'PRESCRIT',
  EN_COURS = 'EN_COURS',
  TERMINE = 'TERMINE',
  ARRETE = 'ARRETE'
}

export interface CreatePrescriptionRequest {
  consultationId: number;
  dureeTraitement: number;
  instructionsGenerales: string;
}

export interface CreateMedicamentRequest {
  nomMedicament: string;
  dosage: string;
  frequence: string;
  duree: number;
  instructionsSpecifiques: string;
  quantitePrescrite: number;
}

@Injectable({
  providedIn: 'root'
})
export class PrescriptionService {
  private apiUrl = 'http://localhost:8081/api/v1/prescriptions';

  constructor(private http: HttpClient) {}

  creerPrescription(request: CreatePrescriptionRequest): Observable<Prescription> {
    return this.http.post<Prescription>(this.apiUrl, request);
  }

  ajouterMedicament(prescriptionId: number, request: CreateMedicamentRequest): Observable<MedicamentPrescrit> {
    return this.http.post<MedicamentPrescrit>(`${this.apiUrl}/${prescriptionId}/medicaments`, request);
  }

  mettreAJourStatutMedicament(medicamentId: number, statut: StatutMedicament): Observable<MedicamentPrescrit> {
    const params = new HttpParams().set('statut', statut);
    return this.http.put<MedicamentPrescrit>(`${this.apiUrl}/medicaments/${medicamentId}/statut`, null, { params });
  }

  terminerPrescription(id: number): Observable<Prescription> {
    return this.http.put<Prescription>(`${this.apiUrl}/${id}/terminer`, {});
  }

  getPrescriptionsActives(patientId: number): Observable<Prescription[]> {
    return this.http.get<Prescription[]>(`${this.apiUrl}/patient/${patientId}/actives`);
  }

  getPrescriptionById(id: number): Observable<Prescription> {
    return this.http.get<Prescription>(`${this.apiUrl}/${id}`);
  }
}