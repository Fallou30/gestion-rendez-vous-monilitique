import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExportService {
  private baseUrl = 'http://localhost:8081/export';

  constructor(private http: HttpClient) {}

  /**
   * Exporte la liste des patients au format CSV
   */
  exporterPatientsCSV(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/patients/csv`, {
      responseType: 'blob'
    });
  }

  /**
   * Exporte les consultations entre deux dates au format JSON
   * @param dateDebut Date de début (format ISO yyyy-MM-dd)
   * @param dateFin Date de fin (format ISO yyyy-MM-dd)
   */
  exporterConsultationsJSON(dateDebut: string, dateFin: string): Observable<Blob> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    return this.http.get(`${this.baseUrl}/consultations/json`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Génère le rapport PDF d'un patient par ID
   * @param patientId Identifiant du patient
   */
  genererRapportPDF(patientId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/patients/${patientId}/rapport-pdf`, {
      responseType: 'blob'
    });
  }
}
