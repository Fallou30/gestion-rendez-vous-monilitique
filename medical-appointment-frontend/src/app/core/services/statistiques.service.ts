import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MedicamentPrescritStat {
  nomMedicament: string;
  nombrePrescriptions: number;
  quantiteTotale: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatistiqueService {
  private apiUrl = 'http://localhost:8081/api/v1/statistiques';

  constructor(private http: HttpClient) {}

  getStatistiquesConsultationsMedecin(medecinId: number, dateDebut: string, dateFin: string): Observable<Map<string, any>> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get<Map<string, any>>(`${this.apiUrl}/consultations/medecin/${medecinId}`, { params });
  }

  getStatistiquesPrescriptions(dateDebut: string, dateFin: string): Observable<Map<string, any>> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get<Map<string, any>>(`${this.apiUrl}/prescriptions`, { params });
  }

  getTopMedicamentsPrescrits(limit: number = 10): Observable<MedicamentPrescritStat[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<MedicamentPrescritStat[]>(`${this.apiUrl}/medicaments/top`, { params });
  }
}
